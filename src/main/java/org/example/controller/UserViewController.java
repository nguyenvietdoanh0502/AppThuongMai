package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.example.api.CallApiProduct;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserViewController implements Initializable {
    @FXML
    private TilePane productContainer;
    private ProductService productService = ProductServiceImpl.getInstance();
    private CallApiProduct apiProduct = new CallApiProduct();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Product> products = null;
        try {
            products = apiProduct.getAllProducts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try{
            for(Product x: products){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/ProductCard.fxml"));
                VBox productBox = fxmlLoader.load();
                ProductCardController cardController = fxmlLoader.getController();
                cardController.setData(x);
                productContainer.getChildren().add(productBox);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
