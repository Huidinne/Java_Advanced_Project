package presentation;

import model.Booking;
import model.PreparationStatus;
import model.User;
import service.WorkflowService;
import util.InputUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupportMenu {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final WorkflowService workflowService = new WorkflowService();
    private final User currentUser;

    public SupportMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                               MENU HỖ TRỢ
                    ================================
                    | 1 | Công việc được phân công |
                    | 2 | Cập nhật chuẩn bị        |
                    | 0 | Đăng xuất                |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> viewAssignedWork();
                case 2 -> updatePreparationStatus();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void viewAssignedWork() {
        List<Booking> list = workflowService.getAssignedBookings(currentUser.getId());
        if (list.isEmpty()) {
            System.out.println("Hiện chưa có công việc nào được phân công cho bạn");
            return;
        }

        System.out.println("===========================================================================================");
        System.out.println("ID | Room | Start Time         | End Time           | Trạng thái chuẩn bị");
        System.out.println("-------------------------------------------------------------------------------------------");
        for (Booking b : list) {
            System.out.printf("%-2d | %-4d | %-18s | %-18s | %-18s%n",
                    b.getId(),
                    b.getRoomId(),
                    b.getStartTime().format(DATE_TIME_FORMATTER),
                    b.getEndTime().format(DATE_TIME_FORMATTER),
                    b.getPreparationStatus().name());
        }
        System.out.println("===========================================================================================");
    }

    private void updatePreparationStatus() {
        int bookingId = InputUtil.inputInt("Nhập ID booking được giao: ");
        PreparationStatus status = inputPreparationStatus();

        System.out.print("Xác nhận cập nhật trạng thái? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Đã hủy thao tác");
            return;
        }

        try {
            boolean ok = workflowService.updatePreparationStatus(currentUser.getId(), bookingId, status);
            if (ok) {
                System.out.println("Cập nhật trạng thái thành công");
            } else {
                System.out.println("Cập nhật thất bại: booking không được giao cho bạn hoặc không hợp lệ");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi cập nhật trạng thái: " + e.getMessage());
        }
    }

    private PreparationStatus inputPreparationStatus() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    Chọn trạng thái chuẩn bị:
                    | 1 | PREPARING        |
                    | 2 | READY            |
                    | 3 | MISSING_EQUIPMENT|
                    Chọn: """);
            switch (choice) {
                case 1:
                    return PreparationStatus.PREPARING;
                case 2:
                    return PreparationStatus.READY;
                case 3:
                    return PreparationStatus.MISSING_EQUIPMENT;
                default:
                    System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }


    private boolean confirmYesNo() {
        while (true) {
            String input = InputUtil.inputString("");
            if ("y".equalsIgnoreCase(input)) {
                return true;
            }
            if ("n".equalsIgnoreCase(input)) {
                return false;
            }
            System.out.print("Vui lòng nhập y hoặc n: ");
        }
    }
}
