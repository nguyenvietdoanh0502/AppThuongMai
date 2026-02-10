package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.User;
import org.example.utils.PasswordUtils;

import static org.example.model.Role.ADMIN;

public class UserService {
    private final UserDAO userDao = new UserDAO();

    public void initDefaultAdmin() {
        String defaultAdminName = "admin_root";
        if (userDao.SearchUserName(defaultAdminName) == null) {
            // Luôn lưu mật khẩu đã mã hóa vào DB
            String hashedPw = PasswordUtils.hashPassword("admin123");
            User admin = new User(defaultAdminName, hashedPw, "admin@system.com", ADMIN);
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
        String hashedPassword = PasswordUtils.hashPassword(password);
        User user = new User(username, hashedPassword, email, Role.USER);
        userDao.AddUser(user);
        return true;
    }

    public User searchUser(String username) {
        return userDao.SearchUserName(username);
    }

    public User login(String username, String password) {
        User user = userDao.SearchUserName(username);
        if (user == null) {
            return null;
        }

        if (!PasswordUtils.checkPassword(password, user.getPassword())) {
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
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        return userDao.updatePassword(email, hashedPassword);
    }

    public User findUserById(int id) {
        return userDao.findUserById(id);
    }

    public void deductMoneyById(int id, double total) {
        userDao.deductMoneyById(id, total);
    }

    public User findByEmail(String email) {
        return userDao.searchUserByEmail(email);
    }

    //  Thêm hàm tạo User nhanh cho Google nếu muốn tự động đăng ký
    public User createGoogleUser(String email, String fullName) {
        // Tạo username từ email (ví dụ: nguyenvanan@gmail.com -> nguyenvanan)
        String username = email.split("@")[0];

        // Nếu username đã tồn tại, thêm số ngẫu nhiên
        if (userDao.SearchUserName(username) != null) {
            username = username + System.currentTimeMillis() % 1000;
        }

        // Mật khẩu mặc định ngẫu nhiên vì login qua Google không cần mật khẩu này
        String dummyPassword = PasswordUtils.hashPassword("GoogleLogin_" + System.currentTimeMillis());
        User newUser = new User(username, dummyPassword, email, Role.USER);

        userDao.AddUser(newUser);
        return userDao.searchUserByEmail(email);
    }
}