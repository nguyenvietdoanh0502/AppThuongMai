package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.Status;
import org.example.model.User;

public class UserService {
    private final UserDAO userDao = new UserDAO();

    // 1. Đăng ký tài khoản
    public boolean register(String username, String password, String email) {
        // Cấm dùng username admin đặc biệt
        if (username.equalsIgnoreCase("AdNguyenHien")) {
            return false;
        }

        // Cấm dùng email admin đặc biệt
        if (email.equalsIgnoreCase("hien2k6tta@gmail.com")) {
            return false;
        }

        // Kiểm tra username đã tồn tại chưa
        if (userDao.SearchUserName(username) != null) {
            return false;
        }

        User user = new User(username, password, email, Role.USER);
        userDao.AddUser(user);
        return true;
    }

    // 2. Đăng nhập
    public User login(String username, String password) {
        User user = userDao.SearchUserName(username);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        if (user.getStatus() != Status.ACTIVE) {
            return null;
        }
        return user;
    }

    // 3. Kiểm tra Username và Email có khớp nhau không (Dành cho Quên mật khẩu)
    public boolean checkAccountExist(String username, String email) {
        User user = userDao.SearchUserName(username);
        // Nếu tìm thấy user và email của user đó khớp với email nhập vào (không phân biệt hoa thường)
        return user != null && user.getEmail().equalsIgnoreCase(email);
    }

    // 4. Cập nhật mật khẩu mới sau khi đã xác minh
    public boolean ResetPassword(String email, String newPassword) {
        User userAccount = userDao.ForgotPassword(email);
        if (userAccount == null) {
            return false;
        }
        userAccount.setPassword(newPassword);
        // Lưu ý: Nếu UserDao của bạn cần lệnh save/update thì hãy gọi thêm ở đây
        return true;
    }
}