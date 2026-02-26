package org.example.model.dto;

import org.example.dao.UserDAO;
import org.example.model.User;

public class UserDTO {
    private static UserDTO instance;
    private int userId;
    private String userName;
    private String avatarUrl;
    private UserDAO userDAO = new UserDAO();

    private UserDTO(int userId, String userName, String avatarUrl) {
        this.userId = userId;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }

    public static void login(int userId, String userName, String avatarUrl) {
        instance = new UserDTO(userId, userName, avatarUrl);
    }

    public static UserDTO getInstance() {
        return instance;
    }

    public static void logout() {
        instance = null;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}