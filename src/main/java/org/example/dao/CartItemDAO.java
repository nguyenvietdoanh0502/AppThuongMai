package org.example.dao;


import lombok.NoArgsConstructor;
import org.example.model.CartItem;
import org.example.model.dto.CartItemDTO;
import org.example.model.dto.UserDTO;
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
        String sql ="INSERT INTO cartitems (user_id, product_id, quantity) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
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



    public void removeCartItem(int id){
        String sql = "DELETE FROM CartItems WHERE cart_item_id=?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(id));
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

    public int countCartItem(){
        String sql = "SELECT SUM(quantity) FROM CartItems WHERE user_id = ?";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(UserDTO.getInstance().getUserId()));
            ResultSet res = ps.executeQuery();
            if (res.next()) {
                return res.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();;
        }
        return 0;
    }
    public List<CartItemDTO> getCartItemInfo(){
        String sql = "SELECT p.product_id, " +
                "       p.title, " +
                "       p.image, " +
                "       p.price, " +
                "       c.cart_item_id, " +
                "       c.user_id, " +
                "       c.product_id, " +
                "       c.quantity AS cart_qty, " +
                "       p.quantity AS stock_qty " +
                "FROM cartitems c " +
                "JOIN products p ON c.product_id = p.product_id " +
                "WHERE c.user_id = ? AND p.is_deleted = 0";
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(UserDTO.getInstance().getUserId()));
            ResultSet res = ps.executeQuery();
            while(res.next()){
                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setCartItemId(res.getInt("cart_item_id"));
                cartItemDTO.setUserId(res.getInt("user_id"));
                cartItemDTO.setProductId(res.getInt("product_id"));
                cartItemDTO.setQuantity(res.getInt("cart_qty"));
                cartItemDTO.setStockQty(res.getInt("stock_qty"));
                cartItemDTO.setTitle(res.getString("title"));
                cartItemDTO.setPrice(res.getDouble("price"));
                cartItemDTO.setImage(res.getString("image"));
                cartItemDTOS.add(cartItemDTO);
            }
        }catch (SQLException e){
            e.printStackTrace();;
        }
        return cartItemDTOS;
    }
    public void increaseQuantity(int productId,int newQty){
        String sql = "UPDATE cartitems " +
                "SET quantity = ? " +
                "WHERE user_id = ? AND product_id = ?;";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
                ){
            ps.setString(1, String.valueOf(newQty));
            ps.setString(2, String.valueOf(UserDTO.getInstance().getUserId()));
            ps.setString(3, String.valueOf(productId));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
    public void decreaseQuantity(int productId){
        String sql = "UPDATE cartitems " +
                "SET quantity = quantity - 1 " +
                "WHERE user_id = ? AND product_id = ?;";
        try(
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ){
            ps.setString(1, String.valueOf(UserDTO.getInstance().getUserId()));
            ps.setString(2, String.valueOf(productId));
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
