package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.api.CallApi;
import org.example.model.Product;

import javafx.scene.image.Image;

import javafx.scene.control.Label;
import org.example.service.ProductService;
import org.example.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductInforController implements Initializable {
    public VBox relatedItemsContainer;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblRating;
    @FXML
    private Label lblReview;
    @FXML
    private Label lblDescri;
    @FXML
    private Label lblPrice;
    @FXML
    private ImageView imgProduct;
    private final ProductService productService= ProductServiceImpl.getInstance();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
    public void setData(Product product) throws IOException, InterruptedException {
        lblTitle.setText(product.getTitle());
        lblCategory.setText(product.getCategory());
        lblDescri.setText(product.getDescription());
        lblRating.setText(String.valueOf(product.getRatingRate()));
        lblReview.setText("("+String.valueOf(product.getRatingCount())+" Reviews)");
        lblPrice.setText("$"+String.valueOf(product.getPrice()));
        try{
            if(product.getImage()!=null && !product.getImage().isEmpty()){
                Image image = new Image(product.getImage(),true);
                imgProduct.setImage(image);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        loadRelatedProduct(product);
    }
    public void loadRelatedProduct(Product product) throws IOException, InterruptedException {
        relatedItemsContainer.getChildren().clear();
        List<Product> relatedList = productService.getRelatedProduct(product);
        for(Product x:relatedList){
            System.out.println(x);
        }
        try{
            for(Product p : relatedList){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProductCard.fxml"));
                Parent cardNode = loader.load();
                ProductCardController cardController = loader.getController();
                cardController.setData(p);

                cardNode.setOnMouseClicked(event -> {
                    try {
                        setData(p);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                relatedItemsContainer.getChildren().add(cardNode);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private Runnable backHandler;


    public void setOnBackAction(Runnable action) {
        this.backHandler = action;
    }
    @FXML
    private void handleBack() {
        if (backHandler != null) {
            backHandler.run();
        }
    }
}
