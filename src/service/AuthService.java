package service;

import dao.UserDAO;
import model.Role;
import model.User;
import util.PasswordHash;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public boolean register(String username, String password, String name) {
        if (userDAO.findByUsername(username) != null) {
            System.out.println("Username đã tồn tại");
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordHash.hash(password));
        user.setRole(Role.EMPLOYEE);
        user.setName(name);

        return userDAO.insert(user);
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            System.out.println("User không tồn tại");
            return null;
        }

        if (!PasswordHash.verify(password, user.getPassword())) {
            System.out.println("Sai mật khẩu");
            return null;
        }

        return user;
    }
}
