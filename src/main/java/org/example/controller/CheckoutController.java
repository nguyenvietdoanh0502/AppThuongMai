package org.example.controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.constant.Animation;
import org.example.constant.Regex;
import org.example.model.Order;
import org.example.model.OrderDetail;
import org.example.model.User;
import org.example.model.dto.CartItemDTO;
import org.example.model.dto.UserDTO;
import org.example.service.*;
import org.example.service.impl.CartItemServiceImpl;
import org.example.service.impl.OrderDetailServiceImpl;
import org.example.service.impl.OrderServiceImpl;
import org.example.service.impl.ProductServiceImpl;

import java.net.URL;
import java.util.*;

public class CheckoutController implements Initializable {
    @FXML
    public Button btnCheckoutAction;
    public VBox paneCart;
    public VBox paneShipping;
    public TextField txtFullName;
    public TextField txtPhone;
    public TextField txtAddress;
    public StackPane successOverlay;
    public Label lblOrderId;
    public Button btnContinueShopping;
    @FXML
    private VBox vboxCartItems;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblGrandTotal;
    @FXML
    private Button btnBack;
    private CartItemService cartItemService = new CartItemServiceImpl();
    private double deliveryChange = 5;
    private Runnable backHandler;
    private Map<Integer, PauseTransition> debounceMap = new HashMap<>();
    private boolean isShippingStep = false;
    private final UserService userService = new UserService();
    private final OrderService orderService = new OrderServiceImpl();
    private ProductService productService = ProductServiceImpl.getInstance();
    private OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadCheckoutData();
        btnCheckoutAction.setOnAction(e->{
            if(!isShippingStep ){
                if(!vboxCartItems.getChildren().isEmpty()){
                    goToShippingStep();
                }
                else{
                    Animation.showAlert("Lỗi","Không có sản phẩm nào trong giỏ hàng!");
                }
            }
            else{
                handlePlaceOrder();
            }
        });
        btnContinueShopping.setOnAction(e->{
            successOverlay.setVisible(false);
            if(backHandler!=null){
                backHandler.run();
            }
        });
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
            Animation.playClickAnimation(btnMinus);
            if(Integer.parseInt(lblQty.getText())>1){

                int newQty = item.getQuantity()-1;
                item.setQuantity(newQty);
                lblQty.setText(String.valueOf(newQty));
                double newSubtotal = newQty * item.getPrice();
                subtotal.setText("$" + String.format("%.2f", newSubtotal));
                updateQuantityWithDelay(item);
            }
            else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Thông báo");
                alert.setHeaderText(null);
                alert.setContentText("Xóa sản phẩm khỏi giỏ hảng?");
                ButtonType buttonTypeYes = new ButtonType("Đồng ý", ButtonBar.ButtonData.YES);
                ButtonType buttonTypeNo = new ButtonType("Không", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonTypeYes){
                    cartItemService.removeCartItem(item.getCartItemId());
                }
                loadCheckoutData();
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
        if(isShippingStep){
            backToCartStep();
        }
        else{
            if (backHandler != null) {
                backHandler.run();
            }
        }

    }
    private void goToShippingStep(){
        paneCart.setVisible(false);
        paneShipping.setVisible(true);
        btnCheckoutAction.setText("Place Order");
        btnBack.setText("Back to Cart");
        isShippingStep = true;
    }
    private void backToCartStep(){
        paneShipping.setVisible(false);
        paneCart.setVisible(true);
        btnCheckoutAction.setText("Proceed to Checkout");
        btnBack.setText("Continue Shopping");
        isShippingStep = false;
    }
    private void handlePlaceOrder(){
        if(txtAddress.getText().isEmpty() || txtPhone.getText().isEmpty() ){
            Animation.showAlert("Lỗi","Vui lòng điền đầy đủ thông tin!");
            return;
        }
        if(!txtPhone.getText().trim().matches(Regex.PHONENUMBER_PATTERN)){
            Animation.showAlert("Lỗi","Số điện thọai không hợp lệ!");
            return;
        }
        try {
            int id = UserDTO.getInstance().getUserId();
            double total = 0;
            List<CartItemDTO> items = cartItemService.getCartItemInfo();
            for (CartItemDTO dto : items) {
                total += dto.getPrice() * dto.getQuantity();
            }
            total+=5;
            User user = userService.findUserById(id);
            if(total<=user.getMoney()){
                Order order = new Order();
                order.setAddress(txtAddress.getText());
                order.setPhoneNumber(txtPhone.getText());
                order.setUserId(id);
                order.setTotalAmount(total);
                orderService.addOrder(order);
                userService.deductMoneyById(id,total);
                for(CartItemDTO i:items){
                    OrderDetail orderDetail = new OrderDetail();
                    productService.reduceQuantityById(i.getProductId(),i.getQuantity());
                    orderDetail.setOrderId(order.getOrderId());
                    orderDetail.setPrice(i.getPrice());
                    orderDetail.setQuantity(i.getQuantity());
                    orderDetail.setProductId(i.getProductId());
                    orderDetailService.addOrderDetail(orderDetail);
                    cartItemService.removeCartItem(i.getCartItemId());
                }
                showSuccessOverlay();
            }
            else{
                Animation.showAlert("Thông báo","Tài khoản không đủ");
            }

        } finally {

        }
    }
    private void showSuccessOverlay() {
        successOverlay.setOpacity(0);
        successOverlay.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(300), successOverlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

}
