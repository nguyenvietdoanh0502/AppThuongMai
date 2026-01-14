package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.model.Product;

import javafx.scene.image.Image;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ProductCardController implements Initializable {

    @FXML
    private VBox cardContainer;
    @FXML
    private ImageView imgProduct;

    @FXML
    private Label lblTitle;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupHoverEffect();
    }
    public void setData(Product product){
        lblTitle.setText(product.getTitle());
        lblPrice.setText("$"+String.valueOf(product.getPrice()));
        lblCategory.setText(product.getCategory());
        try{
            if(product.getImage()!=null && !product.getImage().isEmpty()){
                Image image = new Image(product.getImage(),true);
                imgProduct.setImage(image);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setupHoverEffect() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.1)); // Bóng mờ mặc định
        cardContainer.setEffect(dropShadow);

        cardContainer.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), cardContainer);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();

            dropShadow.setRadius(20);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            cardContainer.setStyle("-fx-cursor: hand; -fx-background-color: white; -fx-background-radius: 10;");
        });

        cardContainer.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), cardContainer);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            dropShadow.setRadius(10);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
            cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        });
    }


}
