package service;

import dao.BookingDAO;
import dao.BookingDetailDAO;
import dao.EquipmentDAO;
import dao.RoomDAO;
import dao.ServiceDAO;
import model.*;
import util.DBConnection;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingService {

	private final BookingDAO bookingDAO = new BookingDAO();
	private final BookingDetailDAO bookingDetailDAO = new BookingDetailDAO();
	private final RoomDAO roomDAO = new RoomDAO();
	private final EquipmentDAO equipmentDAO = new EquipmentDAO();
	private final ServiceDAO serviceDAO = new ServiceDAO();

	public List<Room> getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
		validateTimeRange(startTime, endTime);
		return roomDAO.findAvailableRooms(startTime, endTime);
	}

	public boolean hasTimeConflict(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
		validateTimeRange(startTime, endTime);
		return bookingDAO.existsConflict(roomId, startTime, endTime);
	}

	public boolean createPendingBooking(int userId, int roomId, LocalDateTime startTime, LocalDateTime endTime,
										List<BookingDetail> equipmentDetails) {
		return createPendingBooking(userId, roomId, startTime, endTime, 1, equipmentDetails);
	}

	public boolean createPendingBooking(int userId, int roomId, LocalDateTime startTime, LocalDateTime endTime,
										int attendeeCount, List<BookingDetail> requestedDetails) {
		validateTimeRange(startTime, endTime);
		if (attendeeCount <= 0) {
			throw new IllegalArgumentException("Số người tham gia phải lớn hơn 0");
		}

		Room room = roomDAO.findById(roomId);
		if (room == null) {
			throw new IllegalArgumentException("Phòng không tồn tại");
		}
		if (room.getStatus() != RoomStatus.AVAILABLE) {
			throw new IllegalArgumentException("Phòng hiện không khả dụng");
		}
		if (attendeeCount > room.getCapacity()) {
			throw new IllegalArgumentException("Số người vượt quá sức chứa của phòng");
		}

		List<BookingDetail> normalizedDetails = normalizeAndValidateDetails(requestedDetails);

		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);

			if (bookingDAO.existsConflict(roomId, startTime, endTime, conn)) {
				conn.rollback();
				return false;
			}

			Booking booking = new Booking();
			booking.setUserId(userId);
			booking.setRoomId(roomId);
			booking.setStartTime(startTime);
			booking.setEndTime(endTime);
			booking.setStatus(BookingStatus.PENDING);
			booking.setSupportStaffId(null);

			int bookingId = bookingDAO.insert(booking, conn);

			for (BookingDetail detail : normalizedDetails) {
				if (detail.getType() == DetailType.EQUIPMENT) {
					Equipment equipment = equipmentDAO.findById(detail.getRefId(), conn);
					if (equipment == null) {
						throw new IllegalArgumentException("Thiết bị ID " + detail.getRefId() + " không tồn tại");
					}

					if (!equipmentDAO.reserveAvailableQuantity(detail.getRefId(), detail.getQuantity(), conn)) {
						throw new IllegalArgumentException("Thiết bị ID " + detail.getRefId() + " không đủ số lượng khả dụng");
					}
				} else {
					if (serviceDAO.findById(detail.getRefId()) == null) {
						throw new IllegalArgumentException("Dịch vụ ID " + detail.getRefId() + " không tồn tại");
					}
				}

				detail.setBookingId(bookingId);
			}

			if (!bookingDetailDAO.insertBatch(normalizedDetails, conn)) {
				throw new RuntimeException("Không thể lưu chi tiết booking");
			}

			conn.commit();
			return true;

		} catch (Exception e) {
			throw new RuntimeException("Lỗi tạo booking", e);
		}
	}

	public List<Booking> getBookingHistoryByUser(int userId) {
		return bookingDAO.findByUserId(userId);
	}

	public String getPreparationSummary(int bookingId) {
		List<BookingDetail> details = bookingDetailDAO.findByBookingId(bookingId);
		if (details.isEmpty()) {
			return "Không có";
		}

		List<String> parts = new ArrayList<>();
		for (BookingDetail detail : details) {
			if (detail.getType() == DetailType.EQUIPMENT) {
				Equipment equipment = equipmentDAO.findById(detail.getRefId());
				String name = equipment == null ? "TB#" + detail.getRefId() : equipment.getName();
				parts.add("Thiết bị: " + name + " x" + detail.getQuantity());
			} else {
				model.Service service = serviceDAO.findById(detail.getRefId());
				String name = service == null ? "DV#" + detail.getRefId() : service.getName();
				parts.add("Dịch vụ: " + name + " x" + detail.getQuantity());
			}
		}

		return String.join(" | ", parts);
	}

	public boolean cancelPendingBooking(int userId, int bookingId) {
		try (Connection conn = DBConnection.getConnection()) {
			conn.setAutoCommit(false);

			Booking booking = bookingDAO.findByIdAndUserId(bookingId, userId, conn);
			if (booking == null || booking.getStatus() != BookingStatus.PENDING) {
				conn.rollback();
				return false;
			}

			List<BookingDetail> details = bookingDetailDAO.findByBookingId(bookingId, conn);
			for (BookingDetail detail : details) {
				if (detail.getType() == DetailType.EQUIPMENT) {
					equipmentDAO.increaseAvailableQuantity(detail.getRefId(), detail.getQuantity(), conn);
				}
			}

			boolean updated = bookingDAO.updateStatusIfCurrent(
					bookingId,
					userId,
					BookingStatus.PENDING,
					BookingStatus.CANCELLED,
					conn
			);

			if (!updated) {
				conn.rollback();
				return false;
			}

			conn.commit();
			return true;
		} catch (Exception e) {
			throw new RuntimeException("Lỗi hủy booking", e);
		}
	}

	private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime == null || endTime == null) {
			throw new IllegalArgumentException("Thời gian không được để trống");
		}
		if (startTime.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Thời gian bắt đầu không được ở quá khứ");
		}
		if (!endTime.isAfter(startTime)) {
			throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
		}
	}

	private List<BookingDetail> normalizeAndValidateDetails(List<BookingDetail> details) {
		if (details == null || details.isEmpty()) {
			return new ArrayList<>();
		}

		Map<String, Integer> aggregate = new HashMap<>();
		for (BookingDetail detail : details) {
			if (detail == null) {
				continue;
			}
			if (detail.getType() != DetailType.EQUIPMENT && detail.getType() != DetailType.SERVICE) {
				throw new IllegalArgumentException("Chi tiết booking không hợp lệ");
			}
			if (detail.getRefId() <= 0) {
				throw new IllegalArgumentException("ID thiết bị/dịch vụ không hợp lệ");
			}
			if (detail.getQuantity() <= 0) {
				throw new IllegalArgumentException("Số lượng yêu cầu phải > 0");
			}

			String key = detail.getType().name() + "_" + detail.getRefId();
			aggregate.put(key, aggregate.getOrDefault(key, 0) + detail.getQuantity());
		}

		List<BookingDetail> normalized = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : aggregate.entrySet()) {
			String[] parts = entry.getKey().split("_");
			BookingDetail copy = new BookingDetail();
			copy.setType(DetailType.valueOf(parts[0]));
			copy.setRefId(Integer.parseInt(parts[1]));
			copy.setQuantity(entry.getValue());
			normalized.add(copy);
		}
		return normalized;
	}
}
