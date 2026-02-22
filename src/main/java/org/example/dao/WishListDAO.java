package org.example.dao;

import org.example.model.dto.WishListDTO;
import org.example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishListDAO {
    public void addWishList(int userId, int productId){
        String sql = "INSERT INTO wishlist (user_id, product_id) VALUES (?,?)";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setInt(1,userId);
            ps.setInt(2,productId);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public List<WishListDTO> getAllWishList(int userId){
        List<WishListDTO> list = new ArrayList<>();
        String sql = "SELECT p.product_id, p.title, p.price, p.image FROM wishlist w JOIN products p ON w.product_id = p.product_id WHERE w.user_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setInt(1,userId);
            ResultSet res  = ps.executeQuery();
            if(res.next()){
                WishListDTO wishListDTO = new WishListDTO();
                wishListDTO.setProductId(res.getInt("product_id"));
                wishListDTO.setTitle(res.getString("title"));
                wishListDTO.setPrice(res.getDouble("price"));
                wishListDTO.setImageUrl(res.getString("image"));
                list.add(wishListDTO);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public boolean checkWishList(int userId, int productId){
        String sql = "SELECT * FROM wishlist WHERE user_id = ? and product_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setInt(1,userId);
            ps.setInt(2,productId);
            ResultSet res  = ps.executeQuery();
            if(res.next()){
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void deleteWishList(int userId, int productId){
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND product_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setInt(1,userId);
            ps.setInt(2,productId);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
