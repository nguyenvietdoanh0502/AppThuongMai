package org.example.dao;

import org.example.model.Role;
import org.example.model.Status;
import org.example.model.User;
import org.example.utils.JDBCUtils;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class UserDAO {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setProvider(rs.getString("provider"));

        try {
            user.setMoney(rs.getDouble("money"));
            String statusStr = rs.getString("status");
            if (statusStr != null) {
                user.setStatus(Status.valueOf(statusStr.toUpperCase()));
            } else {
                user.setStatus(Status.ACTIVE);
            }
        } catch (SQLException | IllegalArgumentException ignored) {
            user.setStatus(Status.ACTIVE);
        }

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                user.setRole(Role.valueOf(roleStr.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(Role.USER);
            }
        } else {
            user.setRole(Role.USER);
        }
        return user;
    }

    public User searchUserByEmailAndProvider(String email, String provider) {
        String sql = "SELECT * FROM users WHERE email = ? AND provider = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, provider);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void AddUser(User user) {
        String sql = "INSERT INTO users (username, password, email, role, status, provider) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getStatus().name());
            ps.setString(6, user.getProvider());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deductMoneyById(int id, double total) {
        String sql = "UPDATE users SET money = money - ? WHERE user_id = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User SearchUserName(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUserStatus(int userId, Status status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsersOnly() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'USER'";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}