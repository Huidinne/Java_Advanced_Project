package presentation;

import model.User;
import service.AuthService;
import util.InputUtil;

public class AuthMenu {

    private AuthService authService = new AuthService();

    public void show() {
        while (true) {
            int choice = InputUtil.inputInt("1. Register\n2. Login\nChọn: ");

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
            }
        }
    }

    private void register() {
        String username = InputUtil.inputString("Username: ");
        String password = InputUtil.inputString("Password: ");
        String name = InputUtil.inputString("Name: ");

        if (authService.register(username, password, name)) {
            System.out.println("Đăng ký thành công");
        }
    }

    private void login() {
        String username = InputUtil.inputString("Username: ");
        String password = InputUtil.inputString("Password: ");

        User user = authService.login(username, password);

        if (user != null) {
            System.out.println("Login thành công: " + user.getRole());
        }
    }
}
