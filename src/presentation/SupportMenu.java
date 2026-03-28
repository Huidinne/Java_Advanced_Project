package presentation;

import util.InputUtil;

public class SupportMenu {

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                        MENU HỖ TRỢ
                    ================================
                    | 1 | Xem danh sách phòng      |
                    | 2 | Xem danh sách thiết bị   |
                    | 3 | Xem thông tin booking    |
                    | 0 | Đăng xuất                |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> viewRooms();
                case 2 -> viewEquipment();
                case 3 -> viewBookings();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void viewRooms() {
        System.out.println("Chức năng xem phòng đang được phát triển...");
    }

    private void viewEquipment() {
        System.out.println("Chức năng xem thiết bị đang được phát triển...");
    }

    private void viewBookings() {
        System.out.println("Chức năng xem booking đang được phát triển...");
    }
}
