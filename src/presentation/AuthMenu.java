package presentation;

import model.Role;
import model.User;
import service.AuthService;
import util.InputUtil;
import util.Validator;

public class AuthMenu {

    private AuthService authService = new AuthService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("""
            ==============================
                    AUTH MENU
            ==============================
            | 1 | Register               |
            | 2 | Login                  |
            | 0 | Exit                   |
            ==============================
            Chọn: """);


            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 0 -> { return; }
                default -> System.out.println("Không hợp lệ");
            }
        }
    }

    private void register() {
        String username;
        while (true) {
            username = InputUtil.inputString("Username: ");
            if (Validator.isBlank(username)) {
                System.out.println("Username không được để trống");
            } else break;
        }

        String password;
        while (true) {
            password = InputUtil.inputString("Password: ");
            if (Validator.isBlank(password)) {
                System.out.println("Password không được để trống");
            } else if (!Validator.isStrongPassword(password)) {
                System.out.println("Password phải >= 6 ký tự");
            } else break;
        }

        String name;
        while (true) {
            name = InputUtil.inputString("Name: ");
            if (Validator.isBlank(name)) {
                System.out.println("Name không được để trống");
            } else break;
        }

        String phone;
        while (true) {
            phone = InputUtil.inputString("Phone: ");
            if (!Validator.isBlank(phone) && !Validator.isPhoneValid(phone)) {
                System.out.println("Số điện thoại không hợp lệ");
            } else break;
        }

        String department;
        while (true) {
            department = InputUtil.inputString("Department: ");
            if (Validator.isBlank(department)) {
                System.out.println("Department không được để trống");
            }else break;
        }

        if (authService.register(username, password, name, phone, department)) {
            System.out.println("Đăng ký thành công");
        }
    }

    private void login() {
        String username;
        while (true) {
            username = InputUtil.inputString("Username: ");
            if (Validator.isBlank(username)) {
                System.out.println("Username không được để trống");
            } else break;
        }

        String password;
        while (true) {
            password = InputUtil.inputString("Password: ");
            if (Validator.isBlank(password)) {
                System.out.println("Password không được để trống");
            } else break;
        }

        User user = authService.login(username, password);

        if (user != null) {
            System.out.println("Login thành công: " + user.getRole());

            if (user.getRole() == Role.ADMIN) {
                new AdminRoomMenu().show();
            }
        }
    }
}
