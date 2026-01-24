package org.example.dao;


import lombok.NoArgsConstructor;
import org.example.model.CartItem;
import org.example.utils.JDBCUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class CartItemDAO {
    public List<CartItem> getAllItems(){
        String sql = "SELECT * FROM CartItems";
        List<CartItem> cartItems = new ArrayList<>();
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ResultSet res = ps.executeQuery();
            while (res.next()){
                CartItem cartItem = new CartItem();
                cartItem.setUserId(res.getInt("user_id"));
                cartItem.setProductId(res.getInt("product_id"));
                cartItem.setQuantity(res.getInt("quantity"));
                cartItems.add(cartItem);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return cartItems;
    }
    public void addCartItem(CartItem cartItem){
        String sql ="INSERT INTO CartItems (user_id,product_id,quantity) VALUES(?,?,?)";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(cartItem.getUserId()));
            ps.setString(2, String.valueOf(cartItem.getProductId()));
            ps.setString(3, String.valueOf(cartItem.getQuantity()));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void increaseQuantity(int productId){
        String sql = "SELECT * FROM CartItems WHERE product_id=?";
        int newQuantity = 0;
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(productId));
            ResultSet res = ps.executeQuery();
            if(res.next()){
                newQuantity = res.getInt("quantity")+1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        String sql1 = "UPDATE CartItems SET quantity = ? WHERE product_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql1);
        ){
            ps.setString(2, String.valueOf(productId));
            ps.setString(1, String.valueOf(newQuantity));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public void decreaseQuantity(int productId){
        String sql = "SELECT * FROM CartItems WHERE product_id=?";
        int newQuantity = 0;
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, String.valueOf(productId));
            ResultSet res = ps.executeQuery();
            if(res.next()){
                newQuantity = res.getInt("quantity")-1;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        String sql1 = "UPDATE CartItems SET quantity = ? WHERE product_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql1);
        ){
            ps.setString(2, String.valueOf(productId));
            ps.setString(1, String.valueOf(newQuantity));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void removeCartItem(CartItem cartItem){
        String sql = "DELETE FROM CartItems WHERE cart-item-id=?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(cartItem.getCartItemId()));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void updateCartItem(CartItem cartItem){
        String sql = "UPDATE CartItems SET product_id = ?, user_id = ?, quantity = ? WHERE cart_item_id=?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(cartItem.getProductId()));
            ps.setString(2, String.valueOf(cartItem.getUserId()));
            ps.setString(3, String.valueOf(cartItem.getQuantity()));
            ps.setString(4, String.valueOf(cartItem.getCartItemId()));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
