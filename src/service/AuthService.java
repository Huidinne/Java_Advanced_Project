package service;

import dao.UserDAO;
import model.Role;
import model.User;
import util.PasswordHash;

import java.util.List;

public class AuthService {

    private UserDAO userDAO = new UserDAO();

    public boolean isUsernameExists(String username) {
        return userDAO.existsByUsername(username);
    }

    private User buildUser(String username, String password, String name, String phone, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordHash.hashPassword(password));
        user.setRole(role);
        user.setName(name);
        user.setPhone(phone);
        return user;
    }


    public boolean register(String username, String password, String name, String phone) {
        if (userDAO.existsByUsername(username)) {
            return false;
        }

        User user = buildUser(username, password, name, phone, Role.EMPLOYEE);
        return userDAO.insert(user);
    }


    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);

        if (user == null) {
            return null;
        }

        if (!PasswordHash.verifyPassword(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    public boolean createUser(String username, String password, String name, String phone, Role role) {
        if (userDAO.existsByUsername(username)) {
            return false;
        }

        User user = buildUser(username, password, name, phone, role);
        return userDAO.insert(user);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public List<User> getUsersByRole(Role role) {
        return userDAO.findByRole(role);
    }

    public boolean deleteUser(int id) {
        return userDAO.delete(id);
    }
}
