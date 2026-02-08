package org.example.dao;

import org.example.model.dto.CustomerDTO;
import org.example.model.dto.ProductRevenueDTO;
import org.example.model.dto.ProductStatsDTO;
import org.example.model.dto.RevenueDTO;
import org.example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardDAO {
    public List<RevenueDTO> getDailyRevenue(){
        List<RevenueDTO> list = new ArrayList<>();
        String sql = "SELECT* FROM v_daily_revenue";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ResultSet res = ps.executeQuery();
            while (res.next()){
                RevenueDTO revenueDTO = new RevenueDTO();
                revenueDTO.setDate(res.getDate("report_date"));
                revenueDTO.setTotalOrders(res.getInt("total_orders"));
                revenueDTO.setRevenue(res.getDouble("total_revenue"));
                list.add(revenueDTO);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        Collections.reverse(list);
        return list;
    }
    public int getCountOrders(){
        String sql = "SELECT COUNT(*) FROM orders";
        int total = 0;
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ResultSet res = ps.executeQuery();
            if(res.next()){
                total = res.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }
    public double getSumOrders(){
        String sql = "SELECT SUM(total_amount) FROM orders";
        double total = 0;
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ResultSet res = ps.executeQuery();
            if(res.next()){
                total = res.getDouble(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return total;
    }
    public List<ProductStatsDTO> getTopSellingProducts(){
        List<ProductStatsDTO> list = new ArrayList<>();
        String sql = "SELECT p.title, SUM(od.quantity) as total_sold "+
                "FROM orderdetails od "+
                "JOIN products p ON od.product_id = p.product_id "+
                "GROUP BY p.title "+
                "ORDER BY total_sold DESC";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ResultSet res = ps.executeQuery();
            while (res.next()){
                ProductStatsDTO productStatsDTO = new ProductStatsDTO();
                productStatsDTO.setProductName(res.getString("title"));
                productStatsDTO.setTotalSold(res.getInt("total_sold"));
                list.add(productStatsDTO);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public List<ProductRevenueDTO> getTopRevenueProducts(){
        List<ProductRevenueDTO> list = new ArrayList<>();
        String sql = "SELECT p.title, SUM(od.quantity * od.unit_price) as total_revenue " +
                "FROM orderdetails od " +
                "JOIN products p ON od.product_id = p.product_id " +
                "GROUP BY p.title " +
                "ORDER BY total_revenue DESC";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ResultSet res = ps.executeQuery();
            while(res.next()){
                ProductRevenueDTO productRevenueDTO = new ProductRevenueDTO();
                productRevenueDTO.setProductName(res.getString("title"));
                productRevenueDTO.setTotalRevenue(res.getDouble("total_revenue"));
                list.add(productRevenueDTO);
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public List<CustomerDTO> getTopCustomers(){
        List<CustomerDTO> list = new ArrayList<>();
        String sql = "SELECT u.username, " +
                "COUNT(o.order_id) AS total_orders, " +
                "SUM(o.total_amount) AS total_spent " +
                "FROM orders o " +
                "JOIN users u ON o.user_id = u.user_id " +
                "GROUP BY u.username " +
                "ORDER BY total_spent DESC";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ResultSet res = ps.executeQuery();
            while (res.next()){
                CustomerDTO customerDTO = new CustomerDTO();
                customerDTO.setUsername(res.getString("username"));
                customerDTO.setTotalSpent(res.getDouble("total_spent"));
                customerDTO.setTotalOrder(res.getInt("total_orders"));
                list.add(customerDTO);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}
