package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
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
import javafx.scene.shape.Circle;
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
import org.example.service.CategoryService;
import org.example.service.ProductService;
import org.example.service.impl.CartItemServiceImpl;
import org.example.service.impl.CategoryServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class UserViewController implements Initializable {
    @FXML public VBox categoryContainer;
    @FXML public TextField minPrice, maxPrice;
    @FXML public Label lblCartCount, lblSubtotal, lblOverlayHeader;
    @FXML public Button lblLogout, btnCheckout, lblUser;
    @FXML public VBox cartOverlay, cartItemsContainer, sidebarFilter;
    @FXML public StackPane cartIconContainer;
    @FXML public ScrollPane contentArea;
    @FXML public ColumnConstraints colSidebar, colContent;
    @FXML private TilePane productContainer;
    @FXML private TextField txtSearch;

    private Node homeViewNode;
    private double currentMinPrice = 0.0;
    private double currentMaxPrice = Double.MAX_VALUE;
    private final ProductService productService = ProductServiceImpl.getInstance();
    private final CategoryService categoryService = new CategoryServiceImpl();
    private final CartItemService cartItemService = new CartItemServiceImpl();

    private Set<String> selectedCategories = new HashSet<>();
    private String currentSearchKeyword = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Khởi tạo Avatar người dùng
        setupUserAvatar();

        // 2. Load dữ liệu sản phẩm và danh mục
        List<Product> products = productService.getAllProduct();
        products.removeIf(x -> x.getQuantity() < 1);
        updateCount();
        loadProducts(products);
        renderCategories(products);

        homeViewNode = contentArea.getContent();

        // 3. Thiết lập các Listener tìm kiếm và lọc giá
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

    /**
     * Thiết lập ảnh đại diện từ Google/Facebook cho nút lblUser
     */
    private void setupUserAvatar() {
        UserDTO user = UserDTO.getInstance();
        if (user != null) {
            String avatarUrl = user.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                // Tải ảnh ngầm (backgroundLoading = true)
                Image avatarImg = new Image(avatarUrl, true);
                ImageView imageView = new ImageView(avatarImg);

                // Chỉnh kích thước
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                imageView.setPreserveRatio(true);

                // Bo tròn ảnh bằng Clip Circle
                Circle clip = new Circle(16, 16, 16);
                imageView.setClip(clip);

                // Hiển thị lên Button
                lblUser.setGraphic(imageView);
                lblUser.setContentDisplay(ContentDisplay.LEFT);
                lblUser.setGraphicTextGap(10);
            }
            lblUser.setText(user.getUserName());
        }
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
                productBox.setOnMouseClicked(mouseEvent -> showProductInfor(x));
                productContainer.getChildren().add(productBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCount(){
        int count = cartItemService.countCartItems();
        lblCartCount.setText(count > 9 ? "9+" : String.valueOf(count));
        boolean isVisible = count > 0;
        lblCartCount.setVisible(isVisible);
        if (isVisible) {
            ScaleTransition pulse = new ScaleTransition(Duration.millis(150), lblCartCount);
            pulse.setFromX(1.0); pulse.setFromY(1.0);
            pulse.setToX(1.5); pulse.setToY(1.5);
            pulse.setCycleCount(2); pulse.setAutoReverse(true);
            pulse.play();
        }
    }

    private void applyAllFilters(List<Product> masterData) {
        List<Product> filteredList = new ArrayList<>();
        for (Product p : masterData) {
            boolean matchKeyword = currentSearchKeyword.isEmpty() || p.getTitle().toLowerCase().contains(currentSearchKeyword);
            boolean matchCategory = selectedCategories.isEmpty() || selectedCategories.contains(p.getCategory().toLowerCase());
            boolean matchPrice = p.getPrice() >= this.currentMinPrice && p.getPrice() <= this.currentMaxPrice;

            if (matchKeyword && matchCategory && matchPrice) filteredList.add(p);
        }
        loadProducts(filteredList);
    }

    private void renderCategories(List<Product> products){
        List<Category> categories = categoryService.getAllCategories();
        categoryContainer.getChildren().clear();
        for(Category cat: categories){
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            CheckBox checkBox = new CheckBox();
            String rawName = cat.getName();
            Label label = new Label(rawName.substring(0, 1).toUpperCase() + rawName.substring(1));
            label.setFont(new Font(14));
            row.getChildren().addAll(checkBox, label);
            categoryContainer.getChildren().add(row);
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) selectedCategories.add(cat.getName().toLowerCase());
                else selectedCategories.remove(cat.getName().toLowerCase());
                applyAllFilters(products);
            });
            label.setOnMouseClicked(e -> checkBox.setSelected(!checkBox.isSelected()));
        }
    }

    private void updatePriceRange(){
        try {
            String minText = minPrice.getText().trim();
            this.currentMinPrice = minText.isEmpty() ? 0 : Double.parseDouble(minText);
            String maxText = maxPrice.getText().trim();
            this.currentMaxPrice = maxText.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxText);
        } catch (NumberFormatException e) {
            minPrice.clear(); maxPrice.clear();
        }
    }

    private void showProductInfor(Product product){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProductInfor.fxml"));
            Parent inforView = loader.load();
            ProductInforController inforController = loader.getController();
            inforController.setData(product);
            inforController.setOnAddToCart(() -> { loadCartData(); updateCount(); });
            inforController.setOnBackAction(() -> {
                restoreSidebar();
                if (homeViewNode != null) contentArea.setContent(homeViewNode);
                txtSearch.clear();
            });
            contentArea.setContent(inforView);
            collapseSidebar();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleBtnLogout(ActionEvent event){
        Animation.playClickAnimation(lblLogout);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn đăng xuất?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                UserDTO.logout();
                try {
                    Parent loginView = FXMLLoader.load(getClass().getResource("/view/WelcomeView.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(loginView, 1050, 700));
                    stage.centerOnScreen();
                } catch (IOException e) { e.printStackTrace(); }
            }
        });
    }

    private void collapseSidebar() {
        sidebarFilter.setVisible(false);
        sidebarFilter.setManaged(false);
        colSidebar.setPercentWidth(0);
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
        lblOverlayHeader.setText("You have " + cartItemDTOS.size() + " items in your cart");
        double total = 0;
        for(CartItemDTO item : cartItemDTOS){
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 0, 10, 0));
            ImageView img = new ImageView(new Image(item.getImage(), true));
            img.setFitWidth(50); img.setFitHeight(50);

            VBox info = new VBox(2);
            Label name = new Label(item.getTitle());
            name.setStyle("-fx-font-weight: bold;");
            Label price = new Label(item.getQuantity() + " x $" + item.getPrice());
            info.getChildren().addAll(name, price);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnDelete = new Button();
            FontIcon trashIcon = new FontIcon("fas-trash-alt");
            trashIcon.setIconColor(Color.web("#ff4d4d"));
            btnDelete.setGraphic(trashIcon);
            btnDelete.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            btnDelete.setOnAction(e -> {
                cartItemService.removeCartItem(item.getCartItemId());
                loadCartData();
                updateCount();
            });
            row.getChildren().addAll(img, info, spacer, btnDelete);
            cartItemsContainer.getChildren().add(row);
            total += item.getPrice() * item.getQuantity();
        }
        lblSubtotal.setText("$" + total);
    }

    @FXML public void toggleCart() {
        if (!cartOverlay.isVisible()) loadCartData();
        cartOverlay.setVisible(!cartOverlay.isVisible());
    }

    @FXML public void handleCheckout(){
        Animation.playClickAnimation(btnCheckout);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Checkout.fxml"));
            Parent checkoutView = loader.load();
            contentArea.setContent(checkoutView);
            collapseSidebar();
            cartOverlay.setVisible(false);
            CheckoutController checkoutController = loader.getController();
            checkoutController.setOnBackAction(() -> {
                restoreSidebar();
                if (homeViewNode != null) {
                    contentArea.setContent(homeViewNode);
                    setModeCheckout(false);
                    loadProducts(productService.getAllProduct());
                }
            });
            setModeCheckout(true);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setModeCheckout(boolean isCheckout){
        cartIconContainer.setDisable(isCheckout);
        cartIconContainer.setOpacity(isCheckout ? 0.3 : 1.0);
        lblCartCount.setVisible(!isCheckout);
        if (!isCheckout) updateCount();
    }

    @FXML public void handleUser(ActionEvent event) {
        Animation.playClickAnimation(lblUser);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserProfile.fxml"));
            Parent userProfileView = loader.load();
            contentArea.setContent(userProfileView);
            collapseSidebar();
            cartOverlay.setVisible(false);
            UserProfileController controller = loader.getController();
            controller.setOnBackAction(() -> {
                restoreSidebar();
                if (homeViewNode != null) contentArea.setContent(homeViewNode);
            });
        } catch (IOException e) { e.printStackTrace(); }
    }
}