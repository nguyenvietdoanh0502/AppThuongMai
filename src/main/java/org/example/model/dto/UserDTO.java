package org.example.model.dto;

import org.example.dao.UserDAO;
import org.example.model.User;

public class UserDTO {
    private static UserDTO instance;
    private int userId;
    private String userName;
    private UserDAO userDAO = new UserDAO();
    private UserDTO(int userId, String userName){
        this.userId = userId;
        this.userName = userName;
    }
    public static void login(int userId,String userName){
        instance = new UserDTO(userId,userName);
    }
    public static UserDTO getInstance(){
        return instance;
    }
    public static void logout(){
        instance = null;
    }
    public int getUserId(){
        User user = userDAO.SearchUserName(this.userName);
        return user.getUserId();
    }
}
