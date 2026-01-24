package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.User;

public class UserService {
    private final UserDAO userDao = new UserDAO();

    public boolean register(String username, String password, String email) {
        if (username.equalsIgnoreCase("AdNguyenHien") || email.equalsIgnoreCase("hien2k6tta@gmail.com")) {
            return false;
        }

        // Nếu DB lỗi, userDao.SearchUserName sẽ trả về null
        if (userDao.SearchUserName(username) != null) {
            return false;
        }

        User user = new User(username, password, email, Role.USER);
        userDao.AddUser(user);
        return true;
    }

    public User login(String username, String password) {
        User user = userDao.SearchUserName(username);
        // Kiểm tra thêm điều kiện null để tránh NullPointerException khi so sánh password
        if (user == null || !user.getPassword().equals(password) ) {
            return null;
        }
        return user;
    }

    public boolean checkAccountExist(String username, String email) {
        User user = userDao.SearchUserName(username);
        return user != null && user.getEmail().equalsIgnoreCase(email);
    }

    public boolean ResetPassword(String email, String newPassword) {
        User userAccount = userDao.ForgotPassword(email);
        if (userAccount == null) {
            return false;
        }
        return userDao.updatePassword(email, newPassword);
    }
}