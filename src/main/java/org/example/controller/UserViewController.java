package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.api.CallApi;
import org.example.constant.Animation;
import org.example.model.Category;
import org.example.model.Product;
import org.example.model.dto.CartItemDTO;
import org.example.model.dto.UserDTO;
import org.example.service.CartItemService;
import org.example.service.ProductService;
import org.example.service.impl.CartItemServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class UserViewController implements Initializable {
    @FXML
    public VBox categoryContainer;
    public TextField minPrice;
    public TextField maxPrice;
    public Label lblCartCount;
    public Button lblLogout;
    public VBox cartOverlay;
    public VBox cartItemsContainer;
    public Label lblSubtotal;
    public Label lblOverlayHeader;
    public Button btnCheckout;
    private Node homeViewNode;
    public ScrollPane contentArea;
    public VBox sidebarFilter;
    public ColumnConstraints colSidebar;
    public ColumnConstraints colContent;
    private double currentMinPrice = 0.0;
    private double currentMaxPrice = Double.MAX_VALUE;
    @FXML
    private TilePane productContainer;
    private ProductService productService = ProductServiceImpl.getInstance();
    private CallApi apiProduct = new CallApi();
    @FXML
    private TextField txtSearch;
    private Set<String> selectedCategories = new HashSet<>();
    private String currentSearchKeyword = "";
    private CartItemService cartItemService = new CartItemServiceImpl();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<Product> products = productService.getAllProduct();
        updateCount();
        loadProducts(products);
        renderCategories(products);
        homeViewNode = contentArea.getContent();
        List<Product> finalProducts = products;
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            this.currentSearchKeyword = newVal.toLowerCase().trim();
            applyAllFilters(finalProducts);
        });
        minPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePriceRange();
            applyAllFilters(finalProducts);
        });

        maxPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePriceRange();
            applyAllFilters(finalProducts);
        });
    }
    private void loadProducts(List<Product> products){
        productContainer.getChildren().clear();
        try{
            for(Product x: products){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/ProductCard.fxml"));
                VBox productBox = fxmlLoader.load();
                ProductCardController cardController = fxmlLoader.getController();
                cardController.setData(x);
                cardController.setOnAddToCart(() -> {
                    loadCartData();
                    updateCount();
                });
                productBox.setOnMouseClicked(mouseEvent -> {
                    showProductInfor(x);
                });
                productContainer.getChildren().add(productBox);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateCount(){
        if(cartItemService.countCartItems()>9){
            lblCartCount.setText("9+");
        }
        else {
            lblCartCount.setText(String.valueOf(cartItemService.countCartItems()));
        }
        boolean isVisible = cartItemService.countCartItems() > 0;
        lblCartCount.setVisible(isVisible);
        if (isVisible) {
            ScaleTransition pulse = new ScaleTransition(Duration.millis(150), lblCartCount);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.5);
            pulse.setToY(1.5);
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);
            pulse.play();
        }
    }
    private void applyAllFilters(List<Product> masterData) {
        List<Product> filteredList = new ArrayList<>();

        for (Product p : masterData) {
            boolean matchKeyword = false;
            if (currentSearchKeyword.isEmpty()) {
                matchKeyword = true;
            } else {
                if (p.getTitle().toLowerCase().contains(currentSearchKeyword)) {
                    matchKeyword = true;
                }
            }
            boolean matchCategory = false;
            if (selectedCategories.isEmpty()) {
                matchCategory = true;
            } else {
                if (selectedCategories.contains(p.getCategory().toLowerCase())) {
                    matchCategory = true;
                }
            }
            boolean matchPrice = false;
            if(p.getPrice()>=this.currentMinPrice && p.getPrice()<=this.currentMaxPrice){
                matchPrice = true;
            }
            if (matchKeyword && matchCategory && matchPrice) {
                filteredList.add(p);
            }
        }
        loadProducts(filteredList);
    }
    private void renderCategories(List<Product> products){
        List<Category> categories = null;
        try{
            categories = apiProduct.getAllCategories();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        categoryContainer.getChildren().clear();
        for(Category cat: categories){
            HBox row = new HBox();
            row.setSpacing(10);
            row.setAlignment(Pos.CENTER_LEFT);
            CheckBox checkBox = new CheckBox();
            String rawName = cat.getName();
            Label label = new Label(rawName.substring(0, 1).toUpperCase() + rawName.substring(1));
            label.setFont(new Font(14));
            label.setStyle("-fx-text-fill: #333;");
            row.getChildren().addAll(checkBox,label);
            categoryContainer.getChildren().add(row);
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectedCategories.add(cat.getName());
                } else {
                    selectedCategories.remove(cat.getName());
                }
                applyAllFilters(products);
            });

            label.setOnMouseClicked(e -> checkBox.setSelected(!checkBox.isSelected()));
        }
    }
    private void updatePriceRange(){
        try{
            String minText = minPrice.getText().trim();
            if(minText.isEmpty()){
                this.currentMinPrice=0;
            }
            else{
                this.currentMinPrice = Double.parseDouble(minText);
            }
            String maxText = maxPrice.getText().trim();
            if(maxText.isEmpty()){
                this.currentMaxPrice = Double.MAX_VALUE;
            }
            else{
                this.currentMaxPrice = Double.parseDouble(maxText);
            }
        }catch (NumberFormatException e){
            minPrice.clear();
            maxPrice.clear();

        }
    }
    private void showProductInfor(Product product){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProductInfor.fxml"));
            Parent inforView = loader.load();
            ProductInforController inforController = loader.getController();
            inforController.setData(product);
            inforController.setOnAddToCart(() -> {
                loadCartData();
                updateCount();
            });
            inforController.setOnBackAction(()->{
                restoreSidebar();
                if (homeViewNode != null) {
                    contentArea.setContent(homeViewNode);
                }
                txtSearch.clear();
            });
            contentArea.setContent(inforView);
            collapseSidebar();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void handleBtnLogout(ActionEvent event){
        Animation.playClickAnimation(lblLogout);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");
        ButtonType buttonTypeYes = new ButtonType("Có", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("Không", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            UserDTO.logout();
            try {
                Parent loginView = FXMLLoader.load(getClass().getResource("/view/WelcomeView.fxml"));
                Scene loginScene = new Scene(loginView,1050,700);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(loginScene);
                stage.centerOnScreen();
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void collapseSidebar() {
        sidebarFilter.setVisible(false);
        sidebarFilter.setManaged(false);
        colSidebar.setPercentWidth(0);
        colSidebar.setMinWidth(0);
        colSidebar.setPrefWidth(0);
        colSidebar.setMaxWidth(0);
        colContent.setPercentWidth(100);
    }

    public void restoreSidebar() {
        sidebarFilter.setVisible(true);
        sidebarFilter.setManaged(true);
        colSidebar.setPercentWidth(20);
        colContent.setPercentWidth(80);
    }
    private void loadCartData(){
        cartItemsContainer.getChildren().clear();
        List<CartItemDTO> cartItemDTOS = cartItemService.getCartItemInfo();
        lblOverlayHeader.setText("You have "+ cartItemDTOS.size() + " items in your cart");
        double total = 0;
        for(CartItemDTO item:cartItemDTOS){
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 0, 10, 0));
            ImageView img = new ImageView(new Image(item.getImage(), true));
            img.setFitWidth(50);
            img.setFitHeight(50);
            VBox info = new VBox(2);
            Label nameParams = new Label(item.getTitle());
            nameParams.setStyle("-fx-font-weight: bold;");
            Label priceParams = new Label(item.getQuantity() + " x $" + item.getPrice());
            info.getChildren().addAll(nameParams, priceParams);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button btnDelete = new Button();
            FontIcon trashIcon = new FontIcon("fas-trash-alt");
            trashIcon.setIconColor(Color.web("#ff4d4d"));
            trashIcon.setIconSize(16);
            btnDelete.setGraphic(trashIcon);
            btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btnDelete.setOnAction(e -> {
                cartItemService.removeCartItem(item);
                loadCartData();
                updateCount();
            });
            row.getChildren().addAll(img, info,spacer,btnDelete);
            cartItemsContainer.getChildren().add(row);
            total+= item.getPrice()* item.getQuantity();

        }
        lblSubtotal.setText("$"+total);

    }
    @FXML
    public void toggleCart() {
        boolean isCurrentlyVisible = cartOverlay.isVisible();
        if (!isCurrentlyVisible) {
            loadCartData();
            cartOverlay.setVisible(true);
        } else {
            cartOverlay.setVisible(false);
        }
    }
    @FXML
    public void handleCheckout(){
        Animation.playClickAnimation(btnCheckout);
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Checkout.fxml"));
            Parent checkoutView = loader.load();
            contentArea.setContent(checkoutView);
            collapseSidebar();
            cartOverlay.setVisible(false);
            CheckoutController checkoutController = loader.getController();
            checkoutController.setOnBackAction(()->{
                restoreSidebar();
                if (homeViewNode != null) {
                    contentArea.setContent(homeViewNode);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
