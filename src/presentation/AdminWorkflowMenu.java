package presentation;

import model.Booking;
import model.User;
import service.AuthService;
import service.WorkflowService;
import util.InputUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminWorkflowMenu {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final WorkflowService workflowService = new WorkflowService();
    private final AuthService authService = new AuthService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ==================================
                        DUYỆT & PHÂN CÔNG BOOKING
                    ==================================
                    | 1 | Xem booking PENDING       |
                    | 2 | Duyệt booking             |
                    | 3 | Từ chối booking           |
                    | 4 | Phân công support         |
                    | 0 | Quay lại                  |
                    ==================================
                    Chọn: """);

            switch (choice) {
                case 1 -> viewPendingBookings();
                case 2 -> approveBookingFlow();
                case 3 -> rejectBookingFlow();
                case 4 -> assignSupportFlow();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }

    private void viewPendingBookings() {
        List<Booking> list = workflowService.getPendingBookings();
        if (list.isEmpty()) {
            System.out.println("Không có booking PENDING nào");
            return;
        }

        System.out.println("====================================================================================");
        System.out.println("ID | User | Room | Start Time         | End Time           | Preparation");
        System.out.println("------------------------------------------------------------------------------------");
        for (Booking b : list) {
            System.out.printf("%-2d | %-4d | %-4d | %-18s | %-18s | %-16s%n",
                    b.getId(),
                    b.getUserId(),
                    b.getRoomId(),
                    b.getStartTime().format(F),
                    b.getEndTime().format(F),
                    b.getPreparationStatus() == null ? "NOT_ASSIGNED" : b.getPreparationStatus().name());
        }
        System.out.println("====================================================================================");
    }

    private void approveBookingFlow() {
        int bookingId = InputUtil.inputPositiveInt("Nhập ID booking cần duyệt: ");
        if (!InputUtil.inputYesNo("Xác nhận duyệt booking này? (y/n): ")) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            boolean ok = workflowService.approveBooking(bookingId);
            if (ok) {
                System.out.println("Duyệt booking thành công");
            } else {
                System.out.println("Duyệt thất bại: booking không tồn tại, không ở PENDING, hoặc trùng lịch APPROVED");
            }
        } catch (RuntimeException e) {
            System.out.println("Lỗi duyệt booking: " + e.getMessage());
        }
    }

    private void rejectBookingFlow() {
        int bookingId = InputUtil.inputPositiveInt("Nhập ID booking cần từ chối: ");
        if (!InputUtil.inputYesNo("Xác nhận từ chối booking này? (y/n): ")) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            boolean ok = workflowService.rejectBooking(bookingId);
            if (ok) {
                System.out.println("Từ chối booking thành công");
            } else {
                System.out.println("Từ chối thất bại: booking không tồn tại hoặc không ở PENDING");
            }
        } catch (RuntimeException e) {
            System.out.println("Lỗi từ chối booking: " + e.getMessage());
        }
    }

    private void assignSupportFlow() {
        int bookingId = InputUtil.inputPositiveInt("Nhập ID booking đã duyệt cần phân công support: ");
        printSupportStaff();
        int supportId = InputUtil.inputPositiveInt("Nhập ID support staff: ");

        if (!InputUtil.inputYesNo("Xác nhận phân công support cho booking này? (y/n): ")) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            boolean ok = workflowService.assignSupportStaff(bookingId, supportId);
            if (ok) {
                System.out.println("Phân công support thành công");
            } else {
                System.out.println("Phân công thất bại: booking không tồn tại hoặc chưa được APPROVED");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi phân công support: " + e.getMessage());
        }
    }

    private void printSupportStaff() {
        List<User> supports = authService.getUsersByRole(model.Role.SUPPORT);
        if (supports.isEmpty()) {
            System.out.println("Không có support staff nào");
            return;
        }

        System.out.println("============================================================");
        System.out.println("ID | Username        | Tên nhân viên hỗ trợ");
        System.out.println("------------------------------------------------------------");
        for (User u : supports) {
            System.out.printf("%-2d | %-15s | %-25s%n", u.getId(), u.getUsername(), u.getName());
        }
        System.out.println("============================================================");
    }
}

