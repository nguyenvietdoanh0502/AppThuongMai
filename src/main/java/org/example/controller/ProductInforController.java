package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.api.CallApi;
import org.example.constant.Animation;
import org.example.dao.CartItemDAO;
import org.example.model.CartItem;
import org.example.model.Product;

import javafx.scene.image.Image;

import javafx.scene.control.Label;
import org.example.model.dto.UserDTO;
import org.example.service.CartItemService;
import org.example.service.ProductService;
import org.example.service.impl.CartItemServiceImpl;
import org.example.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductInforController implements Initializable {
    public VBox relatedItemsContainer;
    public Button btnTru;
    public Button btnCong;
    public Label lblQty;
    public Label lblRemain;
    public Button btnAddtoCart;
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
    private CartItemService cartItemService = new CartItemServiceImpl();
    private Product currentProduct = null;
    private Runnable onAddToCartCallback;
    private CartItemDAO cartItemDAO = new CartItemDAO();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setOnAddToCart(Runnable callback) {
        this.onAddToCartCallback = callback;
    }
    public void setData(Product product) throws IOException, InterruptedException {
        currentProduct = product;
        lblTitle.setText(product.getTitle());
        lblCategory.setText(product.getCategory());
        lblDescri.setText(product.getDescription());
        lblRating.setText(String.valueOf(product.getRatingRate()));
        lblReview.setText("("+String.valueOf(product.getRatingCount())+" Reviews)");
        lblPrice.setText("$"+String.valueOf(product.getPrice()));
        lblRemain.setText(String.valueOf(product.getQuantity()));
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
                cardController.setOnAddToCart(() -> {
                    if (onAddToCartCallback != null) {
                        onAddToCartCallback.run();
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
    @FXML
    public void handleBtnCong(){
        Animation.playClickAnimation(btnCong);
        if(Integer.parseInt(lblQty.getText())<currentProduct.getQuantity()){
            int quantity = Integer.parseInt(lblQty.getText());
            lblQty.setText(String.valueOf(quantity + 1));
        }
        else{
            Animation.showAlert("Lỗi","Vượt quá số lượng hàng còn trong kho!");
        }
    }
    @FXML
    public void handleBtnTru(){
        Animation.playClickAnimation(btnTru);
        if(Integer.parseInt(lblQty.getText())>1){
            int quantity = Integer.parseInt(lblQty.getText());
            lblQty.setText(String.valueOf(quantity - 1));
        }
    }
    @FXML
    public void handleBtnAddToCart(){
        Animation.playClickAnimation(btnAddtoCart);
        CartItem cartItem = new CartItem(UserDTO.getInstance().getUserId(),currentProduct.getProductId(),Integer.parseInt(lblQty.getText()));
        int qty = cartItemDAO.getQuantityByUserIdAndProductId(UserDTO.getInstance().getUserId(), currentProduct.getProductId());
        if(Integer.parseInt(lblQty.getText())+qty>currentProduct.getQuantity()){
            Animation.showAlert("Lỗi","Hết hàng!");
        }
        else{
            cartItemService.addCartItem(cartItem);
            lblQty.setText("1");
        }

        if (onAddToCartCallback != null) {
            onAddToCartCallback.run();
        }
    }


}
