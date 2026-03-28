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
                    | 1 | Tạo tài khoản Support    |
                    | 2 | Xem danh sách người dùng |
                    | 3 | Xem Support Staff        |
                    | 4 | Xóa người dùng           |
                    | 0 | Thoát                    |
                    ================================
                    Chọn: """);

            switch (choice) {
                case 1 -> createSupportStaff();
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

    private void createSupportStaff() {
        System.out.println("=== TẠO TÀI KHOẢN SUPPORT STAFF ===");

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
            } else
                break;
        }

        String name;
        while (true) {
            name = InputUtil.inputString("Name: ");
            if (Validator.isBlank(name)) {
                System.out.println("Name không được để trống");
            } else
                break;
        }

        String phone;
        while (true) {
            phone = InputUtil.inputString("Phone: ");
            if (!Validator.isBlank(phone) && !Validator.isPhoneValid(phone)) {
                System.out.println("Số điện thoại không hợp lệ");
            } else
                break;
        }

        if (authService.createSupportStaff(username, password, name, phone)) {
            System.out.println("Tạo tài khoản Support Staff thành công");
        } else {
            System.out.println("Tạo tài khoản thất bại");
        }
    }

    private void viewAllUsers() {
        List<User> list = authService.getAllUsers();

        if (list.isEmpty()) {
            System.out.println("Không có người dùng nào");
            return;
        }

        System.out.println("=== DANH SÁCH NGƯỜI DÙNG ===");
        for (User u : list) {
            System.out.printf("ID: %d | Username: %s | Tên: %s | Vai trò: %s%n",
                    u.getId(), u.getUsername(), u.getName(), u.getRole());
        }
    }

    private void viewSupportStaff() {
        List<User> list = authService.getUsersByRole(Role.SUPPORT);

        if (list.isEmpty()) {
            System.out.println("Không có Support Staff nào");
            return;
        }

        System.out.println("=== DANH SÁCH SUPPORT STAFF ===");
        for (User u : list) {
            System.out.printf("ID: %d | Username: %s | Tên: %s%n",
                    u.getId(), u.getUsername(), u.getName());
        }
    }

    private void deleteUser() {
        int id = InputUtil.inputInt("ID người dùng cần xóa: ");

        if (authService.deleteUser(id)) {
            System.out.println("Xóa người dùng thành công");
        } else {
            System.out.println("Xóa người dùng thất bại");
        }
    }
}
