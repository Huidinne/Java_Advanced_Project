package service;

import dao.BookingDAO;
import dao.RoomDAO;
import dao.UserDAO;
import model.*;
import util.DBConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class WorkflowService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();

    public List<Booking> getPendingBookings() {
        return bookingDAO.findPendingBookings();
    }

    public boolean approveBooking(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("ID booking không hợp lệ");
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Booking booking = bookingDAO.findById(bookingId);
            if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
                conn.rollback();
                return false;
            }

            boolean hasConflict = bookingDAO.existsApprovedConflictExcluding(
                    bookingId,
                    booking.getRoomId(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    conn
            );

            if (hasConflict) {
                conn.rollback();
                return false;
            }

            boolean updated = bookingDAO.updateStatusAndPreparation(
                    bookingId,
                    BookingStatus.APPROVED,
                    PreparationStatus.NOT_ASSIGNED,
                    conn
            );

            if (!updated) {
                conn.rollback();
                return false;
            }

            boolean roomUpdated = roomDAO.updateStatus(booking.getRoomId(), RoomStatus.IN_USE, conn);
            if (!roomUpdated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi duyệt booking", e);
        }
    }

    public boolean rejectBooking(int bookingId) {
        if (bookingId <= 0) {
            throw new IllegalArgumentException("ID booking không hợp lệ");
        }
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            boolean updated = bookingDAO.updateStatusAndPreparation(
                    bookingId,
                    BookingStatus.REJECTED,
                    PreparationStatus.NOT_ASSIGNED,
                    conn
            );

            if (!updated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi từ chối booking", e);
        }
    }

    public boolean assignSupportStaff(int bookingId, int supportStaffId) {
        if (bookingId <= 0 || supportStaffId <= 0) {
            throw new IllegalArgumentException("ID booking hoặc support staff không hợp lệ");
        }
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null || booking.getStatus() != BookingStatus.APPROVED) {
            return false;
        }

        User support = userDAO.findById(supportStaffId);
        if (support == null || support.getRole() != Role.SUPPORT) {
            throw new IllegalArgumentException("Nhân viên hỗ trợ không tồn tại hoặc không đúng vai trò SUPPORT");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            boolean assigned = bookingDAO.assignSupportStaff(bookingId, supportStaffId, conn);
            if (!assigned) {
                conn.rollback();
                return false;
            }

            // Khi đã phân công thì chuyển sang PREPARING để support bắt đầu xử lý.
            bookingDAO.updatePreparationStatus(bookingId, supportStaffId, PreparationStatus.PREPARING, conn);
            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phân công nhân viên hỗ trợ", e);
        }
    }

    public List<Booking> getAssignedBookingsByDate(int supportStaffId, LocalDate date) {
        if (supportStaffId <= 0) {
            throw new IllegalArgumentException("ID nhân viên hỗ trợ không hợp lệ");
        }
        if (date == null) {
            throw new IllegalArgumentException("Ngày không hợp lệ");
        }
        return bookingDAO.findAssignedBySupportAndDate(supportStaffId, date);
    }

    public List<Booking> getAssignedBookings(int supportStaffId) {
        if (supportStaffId <= 0) {
            throw new IllegalArgumentException("ID nhân viên hỗ trợ không hợp lệ");
        }
        return bookingDAO.findAssignedBySupport(supportStaffId);
    }

    public boolean updatePreparationStatus(int supportStaffId, int bookingId, PreparationStatus status) {
        if (supportStaffId <= 0 || bookingId <= 0) {
            throw new IllegalArgumentException("ID booking hoặc support staff không hợp lệ");
        }
        if (status == null || status == PreparationStatus.NOT_ASSIGNED) {
            throw new IllegalArgumentException("Trạng thái chuẩn bị không hợp lệ");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            boolean updated = bookingDAO.updatePreparationStatus(bookingId, supportStaffId, status, conn);
            if (!updated) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật trạng thái chuẩn bị", e);
        }
    }
}

