package org.example.dao;

import org.example.model.Role;
import org.example.model.User;
import org.example.utils.JDBCUtils;

import java.sql.*;

public class UserDAO {

    public User SearchUserName(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = JDBCUtils.connectionDB()) {
            if (conn == null) return null; // dòng này để chặn lỗi InvocationTargetException
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void AddUser(User user) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ? )";
        try (Connection conn = JDBCUtils.connectionDB()) {
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getRole().name());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User ForgotPassword(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = JDBCUtils.connectionDB()) {
            if (conn == null) return null;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = JDBCUtils.connectionDB()) {
            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newPassword);
                ps.setString(2, email);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(Integer.parseInt(rs.getString("user_id")));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                // Ép kiểu chuẩn để so khớp với Enum ADMIN hoặc USER
                user.setRole(Role.valueOf(roleStr.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Cảnh báo: Role trong DB không hợp lệ, gán mặc định USER");
                user.setRole(Role.USER);
            }
        } else {
            user.setRole(Role.USER);
        }
        return user;
    }
    public User findUserById(int id){
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = new User();
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(id));
            ResultSet res = ps.executeQuery();
            if(res.next()){
                user.setMoney(res.getDouble("money"));
                user.setUserId(res.getInt("user_id"));
                user.setPassword(res.getString("password"));
                user.setRole(Role.valueOf(res.getString("role")));
                user.setEmail(res.getString("email"));
                user.setUsername(res.getString("username"));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return user;
    }
    public void deductMoneyById(int id,double total){
        String sql = "UPDATE users SET money = money - ? WHERE user_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(total));
            ps.setString(2, String.valueOf(id));
            ps.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}