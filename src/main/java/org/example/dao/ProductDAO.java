package org.example.dao;


import lombok.NoArgsConstructor;
import org.example.model.Product;
import org.example.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ProductDAO {
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (title,price,description,category_name,image,rating_rate,rating_count,quantity) VALUES(?,?,?,?,?,?,?,?)";
        try (
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, product.getTitle());
            ps.setString(2, String.valueOf(product.getPrice()));
            ps.setString(3, product.getDescription());
            ps.setString(4, product.getCategory());
            ps.setString(5, product.getImage());
            ps.setString(6, String.valueOf(product.getRatingRate()));
            ps.setString(7, String.valueOf(product.getRatingCount()));
            ps.setString(8, String.valueOf(product.getQuantity()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAllProducts() {
        String sql = "SELECT p.product_id, p.title, p.price, p.description, p.category_name, p.image, p.rating_rate,p.rating_count, p.quantity FROM products p WHERE is_deleted = 0";
        List<Product> products = new ArrayList<>();
        try (
                Connection conn = JDBCUtils.connectionDB();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ResultSet res = ps.executeQuery(sql);
            while (res.next()) {
                int id = res.getInt("product_id");
                String title = res.getString("title");
                double price = res.getDouble("price");
                String description = res.getString("description");
                String category = res.getString("category_name");
                String image = res.getString("image");
                double ratingRate = res.getDouble("rating_rate");
                int ratingCount = res.getInt("rating_count");
                int quantity = res.getInt("quantity");
                Product product = new Product(id, title, price, description, category, image, ratingRate, ratingCount, quantity);
                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }


    public void updateProduct(Product product) {
        String sql = "UPDATE products SET title=?,price=?,description=?,category_name=?,image=?,rating_rate=?,rating_count=?,quantity=? WHERE product_id=? AND is_deleted = 0";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, product.getTitle());
            ps.setString(2, String.valueOf(product.getPrice()));
            ps.setString(3, product.getDescription());
            ps.setString(4, product.getCategory());
            ps.setString(5, product.getImage());
            ps.setString(6, String.valueOf(product.getRatingRate()));
            ps.setString(7, String.valueOf(product.getRatingCount()));
            ps.setString(8, String.valueOf(product.getQuantity()));
            ps.setInt(9, product.getProductId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteProduct(int id) {
        String sql = "UPDATE products SET is_deleted = 1 WHERE product_id = ?";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE product_id=? AND is_deleted = 0";
        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) {
                    return new Product(
                            res.getInt("product_id"),
                            res.getString("title"),
                            res.getDouble("price"),
                            res.getString("description"),
                            res.getString("category_name"),
                            res.getString("image"),
                            res.getDouble("rating_rate"),
                            res.getInt("rating_count"),
                            res.getInt("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
