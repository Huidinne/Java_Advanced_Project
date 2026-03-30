package presentation;

import model.Booking;
import model.PreparationStatus;
import model.User;
import service.WorkflowService;
import util.InputUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SupportMenu {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
                    | 1 | Cong viec duoc phan cong |
                    | 2 | Cap nhat chuan bi        |
                    | 0 | Đăng xuất                |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> viewAssignedWorkByDate();
                case 2 -> updatePreparationStatus();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void viewAssignedWorkByDate() {
        LocalDate date = inputDate("Nhap ngay (yyyy-MM-dd), bo trong de lay hom nay: ");
        List<Booking> list = workflowService.getAssignedBookingsByDate(currentUser.getId(), date);
        if (list.isEmpty()) {
            System.out.println("Khong co cong viec nao cho ngay da chon");
            return;
        }

        System.out.println("===========================================================================================");
        System.out.println("ID | Room | Start Time         | End Time           | Trang thai chuan bi");
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
        int bookingId = InputUtil.inputInt("Nhap ID booking duoc giao: ");
        PreparationStatus status = inputPreparationStatus();

        System.out.print("Xac nhan cap nhat trang thai? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            boolean ok = workflowService.updatePreparationStatus(currentUser.getId(), bookingId, status);
            if (ok) {
                System.out.println("Cap nhat trang thai chuan bi thanh cong");
            } else {
                System.out.println("Cap nhat that bai: booking khong duoc giao cho ban hoac khong hop le");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Du lieu khong hop le: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Loi cap nhat trang thai: " + e.getMessage());
        }
    }

    private PreparationStatus inputPreparationStatus() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    Chon trang thai chuan bi:
                    | 1 | PREPARING        |
                    | 2 | READY            |
                    | 3 | MISSING_EQUIPMENT|
                    Chon: """);
            switch (choice) {
                case 1:
                    return PreparationStatus.PREPARING;
                case 2:
                    return PreparationStatus.READY;
                case 3:
                    return PreparationStatus.MISSING_EQUIPMENT;
                default:
                    System.out.println("Lua chon khong hop le");
            }
        }
    }

    private LocalDate inputDate(String message) {
        while (true) {
            String raw = InputUtil.inputString(message);
            if (raw.isBlank()) {
                return LocalDate.now();
            }

            try {
                return LocalDate.parse(raw, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Dinh dang khong hop le. Vi du: 2026-03-30");
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
            System.out.print("Vui long nhap y hoac n: ");
        }
    }
}
