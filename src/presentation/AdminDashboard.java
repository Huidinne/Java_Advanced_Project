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
                    | 2 | Quản lý Thiết bị                       |
                    | 3 | Quản lý Người dùng                     |
                    | 4 | Quản lý Dịch vụ đi kèm                 |
                    | 0 | Đăng xuất                              |
                    ================================================
                    Chọn: """);

            switch (choice) {
                case 1 -> new AdminRoomMenu().show();
                case 2 -> new AdminEquipmentMenu().show();
                case 3 -> new AdminUserMenu().show();
                case 4 -> new AdminServiceMenu().show();
                case 0 -> {
                    System.out.println("Đã đăng xuất");
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }
}
