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
        String username = inputUsername();
        String password = inputPassword();
        String name = inputName();
        String phone = inputPhone();

        boolean success = authService.register(username, password, name, phone);

        if (success) {
            System.out.println("Đăng ký thành công");
        } else {
            System.out.println("Username đã tồn tại hoặc lỗi hệ thống");
        }
    }

    private void login() {
        String username = inputUsername();
        String password = inputPassword();

        User user = authService.login(username, password);

        if (user == null) {
            System.out.println("Sai username hoặc password");
            return;
        }

        System.out.println("Đăng nhập thành công: " + user.getRole());

        switch (user.getRole()) {
            case ADMIN -> new AdminDashboard().show();
            case SUPPORT -> new SupportMenu().show();
            case EMPLOYEE -> new EmployeeMenu().show();
        }
    }

    private String inputUsername() {
        while (true) {
            String username = InputUtil.inputString("Username: ");
            if (Validator.isBlank(username)) {
                System.out.println("Username không được để trống");
            } else {
                return username;
            }
        }
    }

    private String inputPassword() {
        while (true) {
            String password = InputUtil.inputString("Password: ");
            if (Validator.isBlank(password)) {
                System.out.println("Password không được để trống");
            } else if (!Validator.isStrongPassword(password)) {
                System.out.println("Password phải >= 6 ký tự");
            } else {
                return password;
            }
        }
    }

    private String inputName() {
        while (true) {
            String name = InputUtil.inputString("Name: ");
            if (Validator.isBlank(name)) {
                System.out.println("Name không được để trống");
            } else {
                return name;
            }
        }
    }

    private String inputPhone() {
        while (true) {
            String phone = InputUtil.inputString("Phone: ");
            if (!Validator.isBlank(phone) && !Validator.isPhoneValid(phone)) {
                System.out.println("Số điện thoại không hợp lệ");
            } else {
                return phone;
            }
        }
    }
}
