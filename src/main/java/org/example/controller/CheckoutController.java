package org.example.controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.constant.Animation;
import org.example.model.dto.CartItemDTO;
import org.example.service.CartItemService;
import org.example.service.impl.CartItemServiceImpl;

import java.net.URL;
import java.util.*;

public class CheckoutController implements Initializable {
    @FXML
    private VBox vboxCartItems;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblGrandTotal;
    @FXML
    private Button btnBack;
    private CartItemService cartItemService = new CartItemServiceImpl();
    private double deliveryChange = 0;
    private Runnable backHandler;
    private Map<Integer, PauseTransition> debounceMap = new HashMap<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCheckoutData();
    }
    private HBox createCartRow(CartItemDTO item) {
        HBox row = new HBox(0);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(90);
        row.setStyle("-fx-border-color: transparent transparent #eee transparent;");

        double COL_PRODUCT_WIDTH = 300.0;
        double COL_PRICE_WIDTH = 100.0;
        double COL_QTY_WIDTH = 150.0;
        double COL_TOTAL_WIDTH = 100.0;


        HBox productCol = new HBox(15);
        productCol.setAlignment(Pos.CENTER_LEFT);
        productCol.setPrefWidth(COL_PRODUCT_WIDTH);

        ImageView img = new ImageView();
        try {
            if (item.getImage() != null) img.setImage(new Image(item.getImage(), true));
        } catch (Exception e) {}
        img.setFitWidth(60);
        img.setFitHeight(60);
        img.setPreserveRatio(true);

        VBox info = new VBox(5);
        info.setAlignment(Pos.CENTER_LEFT);
        Label name = new Label(item.getTitle());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        name.setWrapText(true);
        name.setMaxWidth(200);
        info.getChildren().addAll(name);

        productCol.getChildren().addAll(img, info);

        Label price = new Label("$" + item.getPrice());
        price.setPrefWidth(COL_PRICE_WIDTH);
        price.setAlignment(Pos.CENTER);
        price.setStyle("-fx-font-weight: bold;");

        VBox qtyCol = new VBox();
        qtyCol.setAlignment(Pos.CENTER);
        qtyCol.setPrefWidth(COL_QTY_WIDTH);

        HBox qtyBox = new HBox();
        qtyBox.setAlignment(Pos.CENTER);
        qtyBox.setMaxWidth(100);
        qtyBox.setMaxHeight(30);
        qtyBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 4;");
        Button btnMinus = new Button("-");
        btnMinus.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Label lblQty = new Label(String.valueOf(item.getQuantity()));
        lblQty.setPrefWidth(30);
        lblQty.setAlignment(Pos.CENTER);
        Button btnPlus = new Button("+");
        btnPlus.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        qtyBox.getChildren().addAll(btnMinus, lblQty, btnPlus);
        qtyCol.getChildren().add(qtyBox);
        int maxQty = item.getStockQty();

        Label subtotal = new Label("$" + String.format("%.2f", item.getPrice() * item.getQuantity()));
        subtotal.setPrefWidth(COL_TOTAL_WIDTH);
        subtotal.setAlignment(Pos.CENTER_RIGHT);
        subtotal.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");
        btnPlus.setOnAction(event->{
            if(Integer.parseInt(lblQty.getText())<maxQty){
                Animation.playClickAnimation(btnPlus);
                int newQty = item.getQuantity() + 1;
                item.setQuantity(newQty);
                lblQty.setText(String.valueOf(newQty));
                double newSubtotal = newQty * item.getPrice();
                subtotal.setText("$" + String.format("%.2f", newSubtotal));
                updateQuantityWithDelay(item);
            }
            else{
                Animation.showAlert("Lỗi","Số lượng hàng trong kho không đủ!");
            }



        });
        btnMinus.setOnAction(event -> {
            if(Integer.parseInt(lblQty.getText())>1){
                Animation.playClickAnimation(btnMinus);
                int newQty = item.getQuantity()-1;
                item.setQuantity(newQty);
                lblQty.setText(String.valueOf(newQty));
                double newSubtotal = newQty * item.getPrice();
                subtotal.setText("$" + String.format("%.2f", newSubtotal));
                updateQuantityWithDelay(item);
            }
        });
        row.getChildren().addAll(productCol, price, qtyCol, subtotal);
        return row;
    }
    private void updateGrandTotal() {
        double total = 0;
        List<CartItemDTO> items = cartItemService.getCartItemInfo();
        for (CartItemDTO dto : items) {
            total += dto.getPrice() * dto.getQuantity();
        }

        lblSubtotal.setText("$" + String.format("%.2f", total));
        lblGrandTotal.setText("$" + String.format("%.2f", total));
    }
    public void loadCheckoutData(){
        vboxCartItems.getChildren().clear();
        List<CartItemDTO> items = cartItemService.getCartItemInfo();
        double subtotal = 0;
        for(CartItemDTO x: items){
            vboxCartItems.getChildren().add(createCartRow(x));
            subtotal+=x.getPrice()*x.getQuantity();
        }
        lblSubtotal.setText("$"+String.valueOf(subtotal));
        lblGrandTotal.setText("$"+String.valueOf((subtotal+deliveryChange)));
    }
    private void updateQuantityWithDelay(CartItemDTO item){

        int productId = item.getProductId();
        if (debounceMap.containsKey(productId)) {
            debounceMap.get(productId).stop();
        }
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            cartItemService.increaseQuantity(productId,item.getQuantity());
            debounceMap.remove(productId);
            updateGrandTotal();

        });
        pause.play();
        debounceMap.put(productId,pause);

    }
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
