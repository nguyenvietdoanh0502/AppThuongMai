package org.example.dao;

import org.example.model.Role;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final List<User> users = new ArrayList<>();

    static {
        users.add(new User(
                "AdNguyenHien",
                "AdNguyenHien",
                "hien2k6tta@gmail.com",
                Role.ADMIN
        ));
    }

    //thêm nời dùng
    public void AddUser(User user) {
        users.add(user);
    }

    // tìm người dùng theo tên
    public User SearchUserName(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    //quên mật khẩu
    public User ForgotPassword(String email) {
        for (User usr : users) {
            if (usr.getEmail().equals(email)) {
                return usr;
            }
        }
        return null;
    }
}
