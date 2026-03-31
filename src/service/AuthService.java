package service;

import dao.UserDAO;
import model.Role;
import model.User;
import util.PasswordHash;
import util.Validator;

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
        validateUserInput(username, password, name, phone);
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
        validateUserInput(username, password, name, phone);
        if (role == null) {
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        }
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
        if (id <= 0) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ");
        }
        return userDAO.delete(id);
    }

    private void validateUserInput(String username, String password, String name, String phone) {
        if (Validator.isBlank(username)) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (Validator.isBlank(password) || !Validator.isStrongPassword(password)) {
            throw new IllegalArgumentException("Password phải có ít nhất 6 ký tự");
        }
        if (Validator.isBlank(name)) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        if (!Validator.isBlank(phone) && !Validator.isPhoneValid(phone)) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
    }
}
