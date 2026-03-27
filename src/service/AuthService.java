package service;

import dao.UserDAO;
import model.Role;
import model.User;
import util.PasswordHash;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public boolean register(String username, String password, String name, String phone, String department) {
        if (userDAO.findByUsername(username) != null) {
            System.out.println("Username đã tồn tại");
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordHash.hashPassword(password));
        user.setRole(Role.EMPLOYEE);
        user.setName(name);
        user.setPhone(phone);
        user.setDepartment(department);

        return userDAO.insert(user);
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            System.out.println("User không tồn tại");
            return null;
        }

        if (!PasswordHash.verifyPassword(password, user.getPassword())) {
            System.out.println("Sai mật khẩu");
            return null;
        }

        return user;
    }
}
