package presentation;

import util.InputUtil;

public class EmployeeMenu {

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                        MENU NHÂN VIÊN
                    ================================
                    | 1 | Đặt phòng họp             |
                    | 2 | Xem lịch sử đặt phòng    |
                    | 0 | Đăng xuất                |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> bookRoom();
                case 2 -> viewBookingHistory();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void bookRoom() {
        System.out.println("Chức năng đặt phòng đang được phát triển...");
    }

    private void viewBookingHistory() {
        System.out.println("Chức năng xem lịch sử đặt phòng đang được phát triển...");
    }
}
