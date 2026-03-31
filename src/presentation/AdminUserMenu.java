package presentation;

import model.Role;
import model.User;
import service.AuthService;
import util.InputUtil;
import util.Validator;

import java.util.List;

public class AdminUserMenu {

    private AuthService authService = new AuthService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
                    ================================
                           QUẢN LÝ NGƯỜI DÙNG
                    ================================
                    | 1 | Tạo tài khoản người dùng |
                    | 2 | Xem danh sách người dùng |
                    | 3 | Xem Support Staff        |
                    | 4 | Xóa người dùng           |
                    | 0 | Thoát                    |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> createUser();
                case 2 -> viewAllUsers();
                case 3 -> viewSupportStaff();
                case 4 -> deleteUser();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void createUser() {
        System.out.println("=== TẠO TÀI KHOẢN NGƯỜI DÙNG ===");

        String username;
        while (true) {
            username = InputUtil.inputString("Username: ");
            if (Validator.isBlank(username)) {
                System.out.println("Username không được để trống");
            } else if (authService.isUsernameExists(username)) {
                System.out.println("Username đã tồn tại");
            } else {
                break;
            }
        }

        String password;
        while (true) {
            password = InputUtil.inputString("Password: ");
            if (Validator.isBlank(password)) {
                System.out.println("Password không được để trống");
            } else if (!Validator.isStrongPassword(password)) {
                System.out.println("Password phải >= 6 ký tự");
            } else {
                break;
            }
        }

        String name;
        while (true) {
            name = InputUtil.inputString("Name: ");
            if (Validator.isBlank(name)) {
                System.out.println("Name không được để trống");
            } else {
                break;
            }
        }

        String phone;
        while (true) {
            phone = InputUtil.inputString("Phone: ");
            if (!Validator.isBlank(phone) && !Validator.isPhoneValid(phone)) {
                System.out.println("Số điện thoại không hợp lệ");
            } else {
                break;
            }
        }

        Role role = inputRole();
        try {
            if (authService.createUser(username, password, name, phone, role)) {
                System.out.printf("Tạo tài khoản %s thành công%n", role);
            } else {
                System.out.println("Tạo tài khoản thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Lỗi hệ thống khi tạo tài khoản: " + e.getMessage());
        }
    }

    private Role inputRole() {
        while (true) {
            int roleChoice = InputUtil.inputInt("""
                    Chọn role:
                    | 1 | EMPLOYEE |
                    | 2 | SUPPORT  |
                    Chọn: """);

            switch (roleChoice) {
                case 1:
                    return Role.EMPLOYEE;
                case 2:
                    return Role.SUPPORT;
                default:
                    System.out.println("Role không hợp lệ");
            }
        }
    }

    private void viewAllUsers() {
        List<User> list = authService.getAllUsers();

        if (list.isEmpty()) {
            System.out.println("Không có người dùng nào");
            return;
        }

        System.out.println("================================================================================");
        System.out.println("ID | Username        | Tên người dùng        | Vai trò");
        System.out.println("--------------------------------------------------------------------------------");
        for (User u : list) {
            System.out.printf("%-2d | %-15s | %-20s | %-8s%n",
                    u.getId(), u.getUsername(), u.getName(), u.getRole());
        }
        System.out.println("================================================================================");
    }

    private void viewSupportStaff() {
        List<User> list = authService.getUsersByRole(Role.SUPPORT);

        if (list.isEmpty()) {
            System.out.println("Không có Support Staff nào");
            return;
        }

        System.out.println("============================================================");
        System.out.println("ID | Username        | Tên nhân viên hỗ trợ");
        System.out.println("------------------------------------------------------------");
        for (User u : list) {
            System.out.printf("%-2d | %-15s | %-25s%n",
                    u.getId(), u.getUsername(), u.getName());
        }
        System.out.println("============================================================");
    }

    private void deleteUser() {
        int id = InputUtil.inputPositiveInt("ID người dùng cần xóa: ");

        if (!InputUtil.inputYesNo("Xác nhận xóa người dùng ID " + id + "? (y/n): ")) {
            System.out.println("Đã hủy thao tác xóa người dùng");
            return;
        }

        try {
            if (authService.deleteUser(id)) {
                System.out.println("Xóa người dùng thành công");
            } else {
                System.out.println("Xóa người dùng thất bại");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Dữ liệu không hợp lệ: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Không thể xóa người dùng. Có thể đang được tham chiếu bởi booking.");
        }
    }
}
