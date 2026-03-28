package presentation;

import util.InputUtil;

public class AdminDashboard {

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================================
                            TRANG CHỦ QUẢN TRỊ VIÊN
                    ================================================
                    | 1 | Quản lý Phòng họp                      |
                    | 2 | Quản lý Thiết bị di động               |
                    | 3 | Quản lý Người dùng                     |
                    | 0 | Đăng xuất                              |
                    ================================================
                    Chọn: """);

            switch (choice) {
                case 1 -> new AdminRoomMenu().show();
                case 2 -> new AdminEquipmentMenu().show();
                case 3 -> new AdminUserMenu().show();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }
}
