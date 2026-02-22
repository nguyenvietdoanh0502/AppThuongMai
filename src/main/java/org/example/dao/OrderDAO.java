package org.example.dao;

import org.example.model.Order;
import org.example.utils.JDBCUtils;

import java.sql.*;

public class OrderDAO {
    public void addOrder(Order order){
        String sql = "INSERT INTO orders (user_id,total_amount,shipping_address,phone_number) VALUES (?,?,?,?)";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ){
            ps.setString(1, String.valueOf(order.getUserId()));
            ps.setString(2, String.valueOf(order.getTotalAmount()));
            ps.setString(3,order.getAddress());
            ps.setString(4,order.getPhoneNumber());
            ps.executeUpdate();
            int orderId = 0;
            ResultSet res = ps.getGeneratedKeys();
            if(res.next()){
                orderId = res.getInt(1);
            }
            order.setOrderId(orderId);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
