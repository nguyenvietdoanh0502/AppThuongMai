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
        // 1. Hiệu ứng bóng đổ (DropShadow)
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.1)); // Bóng mờ mặc định
        cardContainer.setEffect(dropShadow);

        // 2. Sự kiện khi RÊ chuột vào (Mouse Entered)
        cardContainer.setOnMouseEntered(event -> {
            // Phóng to nhẹ lên 1.05 lần
            ScaleTransition st = new ScaleTransition(Duration.millis(200), cardContainer);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();

            // Bóng đậm hơn và lan rộng ra
            dropShadow.setRadius(20);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            cardContainer.setStyle("-fx-cursor: hand; -fx-background-color: white; -fx-background-radius: 10;");
        });

        // 3. Sự kiện khi RÚT chuột ra (Mouse Exited)
        cardContainer.setOnMouseExited(event -> {
            // Thu nhỏ về kích thước gốc (1.0)
            ScaleTransition st = new ScaleTransition(Duration.millis(200), cardContainer);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            // Bóng mờ lại như cũ
            dropShadow.setRadius(10);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
            cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        });
    }


}
