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
                        DUYET & PHAN CONG BOOKING
                    ==================================
                    | 1 | Xem booking PENDING       |
                    | 2 | Duyet booking             |
                    | 3 | Tu choi booking           |
                    | 4 | Phan cong support         |
                    | 0 | Quay lai                  |
                    ==================================
                    Chon: """);

            switch (choice) {
                case 1 -> viewPendingBookings();
                case 2 -> approveBookingFlow();
                case 3 -> rejectBookingFlow();
                case 4 -> assignSupportFlow();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Lua chon khong hop le");
            }
        }
    }

    private void viewPendingBookings() {
        List<Booking> list = workflowService.getPendingBookings();
        if (list.isEmpty()) {
            System.out.println("Khong co booking PENDING nao");
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
        int bookingId = InputUtil.inputInt("Nhap ID booking can duyet: ");
        System.out.print("Xac nhan duyet booking nay? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            boolean ok = workflowService.approveBooking(bookingId);
            if (ok) {
                System.out.println("Duyet booking thanh cong");
            } else {
                System.out.println("Duyet that bai: booking khong ton tai, khong o PENDING, hoac trung lich APPROVED");
            }
        } catch (RuntimeException e) {
            System.out.println("Loi duyet booking: " + e.getMessage());
        }
    }

    private void rejectBookingFlow() {
        int bookingId = InputUtil.inputInt("Nhap ID booking can tu choi: ");
        System.out.print("Xac nhan tu choi booking nay? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            boolean ok = workflowService.rejectBooking(bookingId);
            if (ok) {
                System.out.println("Tu choi booking thanh cong");
            } else {
                System.out.println("Tu choi that bai: booking khong ton tai hoac khong o PENDING");
            }
        } catch (RuntimeException e) {
            System.out.println("Loi tu choi booking: " + e.getMessage());
        }
    }

    private void assignSupportFlow() {
        int bookingId = InputUtil.inputInt("Nhap ID booking da duyet can phan cong support: ");
        printSupportStaff();
        int supportId = InputUtil.inputInt("Nhap ID support staff: ");

        System.out.print("Xac nhan phan cong support cho booking nay? (y/n): ");
        if (!confirmYesNo()) {
            System.out.println("Da huy thao tac");
            return;
        }

        try {
            boolean ok = workflowService.assignSupportStaff(bookingId, supportId);
            if (ok) {
                System.out.println("Phan cong support thanh cong");
            } else {
                System.out.println("Phan cong that bai: booking khong ton tai hoac chua duoc APPROVED");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Du lieu khong hop le: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Loi phan cong support: " + e.getMessage());
        }
    }

    private void printSupportStaff() {
        List<User> supports = authService.getUsersByRole(model.Role.SUPPORT);
        if (supports.isEmpty()) {
            System.out.println("Khong co support staff nao");
            return;
        }

        System.out.println("=== DANH SACH SUPPORT STAFF ===");
        for (User u : supports) {
            System.out.printf("ID: %d | Username: %s | Ten: %s%n", u.getId(), u.getUsername(), u.getName());
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

