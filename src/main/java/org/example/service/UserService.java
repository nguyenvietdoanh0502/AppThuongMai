package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.Status;
import org.example.model.User;

import static org.example.model.Role.ADMIN;

public class UserService {
    private final UserDAO userDao = new UserDAO();

    public void initDefaultAdmin() {
        String defaultAdminName = "admin_root";
        if (userDao.searchUserByEmailAndProvider("admin@system.com", "LOCAL") == null) {
            User admin = new User(defaultAdminName, "admin123", "admin@system.com", ADMIN, "LOCAL");
            userDao.AddUser(admin);
            System.out.println(">>> Đã tạo tài khoản Admin mặc định (admin_root/admin123)");
        }
    }

    public boolean register(String username, String password, String email) {
        if (username.equalsIgnoreCase("admin") && password.equals("admin123") && email.equalsIgnoreCase("admin@system.com")) {
            return false;
        }
        if (userDao.SearchUserName(username) != null) {
            return false;
        }
        User user = new User(username, password, email, Role.USER, "LOCAL");
        userDao.AddUser(user);
        return true;
    }
    public User findByEmail(String email) {

        return userDao.searchUserByEmailAndProvider(email, "LOCAL");
    }

    public User createGoogleUser(String email, String fullName) {
        return createSocialUser(email, fullName, "GOOGLE");
    }

    public User login(String username, String password) {
        User user = userDao.SearchUserName(username);
        if (user == null) return null;
        if (user.getStatus() == Status.LOCKED) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa!");
        }
        if (!user.getPassword().equals(password)) return null;
        return user;
    }

    public boolean checkAccountExist(String username, String email) {
        User user = userDao.SearchUserName(username);
        return user != null && user.getEmail().equalsIgnoreCase(email);
    }

    public User checkLogin(String email, String password) {
        User user = userDao.searchUserByEmailAndProvider(email, "LOCAL");
        if (user != null && user.getPassword().equals(password)) {
            if (user.getStatus() == Status.LOCKED) return null;
            return user;
        }
        return null;
    }

    public User loginSocial(String email, String provider) {
        User user = userDao.searchUserByEmailAndProvider(email, provider);
        if (user != null) {
            if (user.getStatus() == Status.LOCKED) return null;
            return user;
        }
        return null;
    }

    public User createSocialUser(String email, String fullName, String provider) {
        String username = email.split("@")[0] + "_" + provider.toLowerCase();
        if (userDao.SearchUserName(username) != null) {
            username = username + System.currentTimeMillis() % 1000;
        }
        String dummyPassword = provider + "_" + System.currentTimeMillis();
        User newUser = new User(username, dummyPassword, email, Role.USER, provider);
        userDao.AddUser(newUser);
        return userDao.searchUserByEmailAndProvider(email, provider);
    }

    public User searchUser(String username) {
        return userDao.SearchUserName(username);
    }

    public boolean ResetPassword(String email, String newPassword) {
        User userAccount = userDao.searchUserByEmailAndProvider(email, "LOCAL");
        if (userAccount == null) return false;
        return userDao.updatePassword(email, newPassword);
    }

    public User findUserById(int id) {
        return userDao.findUserById(id);
    }

    public void deductMoneyById(int id, double total) {
        userDao.deductMoneyById(id, total);
    }
}