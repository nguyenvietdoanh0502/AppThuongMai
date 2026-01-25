package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.Role;
import org.example.model.User;
import org.example.utils.PasswordUtils;

import static org.example.model.Role.ADMIN;

public class UserService {
    private final UserDAO userDao = new UserDAO();

    public boolean register(String username, String password, String email) {
        if (username.equalsIgnoreCase("AdNguyenHien") || email.equalsIgnoreCase("hien2k6tta@gmail.com")) {
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

    public User login(String username, String password) {
        User user = userDao.SearchUserName(username);

        if (user == null)  {
            return null;
        }
        if(user.getRole()==ADMIN){
            if(user.getPassword().equals(password)){
                return user;
            }
            return null;
        }
        if(!PasswordUtils.checkPassword(password,user.getPassword())){
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
}