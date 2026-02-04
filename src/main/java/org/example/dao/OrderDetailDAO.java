package org.example.dao;

import org.example.model.OrderDetail;
import org.example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDetailDAO {
    public void addOrderDetail(OrderDetail orderDetail){
        String sql = "INSERT INTO orderdetails (order_id,product_id,quantity,unit_price) VALUES (?,?,?,?)";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(orderDetail.getOrderId()));
            ps.setString(2, String.valueOf(orderDetail.getProductId()));
            ps.setString(3, String.valueOf(orderDetail.getQuantity()));
            ps.setString(4, String.valueOf(orderDetail.getPrice()));
            ps.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
