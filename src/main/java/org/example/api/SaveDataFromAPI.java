package org.example.api;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.model.Product;
import org.example.utils.JDBCUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
public class SaveDataFromAPI {
    private CallApi callApi = new CallApi();
    public void saveOrUpdateFromApi()  {
        List<Product> apiList = null;
        try{
            apiList = callApi.getAllProducts();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        String sql = "INSERT INTO products (title,price,description,category_name,image,rating_rate,rating_count,quantity, api_id) " +
                "VALUES (?, ?, ?, ?, ?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "title = VALUES(title), " +
                "price = VALUES(price), " +
                "description = VALUES(description), " +
                "category_name= VALUES(category_name), " +
                "image = VALUES(image), " +
                "rating_rate= VALUES(rating_rate), " +
                "rating_count= VALUES(rating_count), " +
                "quantity= VALUES(quantity), " +
                "api_id= VALUES(api_id)"
                ;

        try (Connection conn = JDBCUtils.connectionDB();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (Product product : apiList) {
                ps.setString(1, product.getTitle());
                ps.setString(2, String.valueOf(product.getPrice()));
                ps.setString(3, product.getDescription());
                ps.setString(4, product.getCategory());
                ps.setString(5, product.getImage());
                ps.setString(6, String.valueOf(product.getRatingRate()));
                ps.setString(7, String.valueOf(product.getRatingCount()));
                ps.setString(8, String.valueOf(product.getQuantity()));
                ps.setString(9, String.valueOf(product.getProductId()));
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            System.out.println("Đã đồng bộ xong " + apiList.size() + " sản phẩm!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
