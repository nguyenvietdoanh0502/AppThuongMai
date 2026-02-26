package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.model.dto.OrderHistoryDTO;
import org.example.model.dto.UserDTO;
import org.example.model.dto.WishListDTO;
import org.example.service.WishListService;
import org.example.service.impl.WishListServiceImpl;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {


    @FXML
    public Label lblSidebarName;
    @FXML
    public Button btnInfo, btnHistory, btnDeposit, btnBack, btnLike;
    @FXML
    public VBox viewInfo, viewHistory, viewDeposit, viewWishlist, wishlistContainer;
    @FXML
    public TextField txtEmail, txtDepositAmount, txtUsername, txtRole, txtMoney;
    @FXML
    public ImageView imgAvatar;

    @FXML
    private TableView<OrderHistoryDTO> tblHistory;
    @FXML
    private TableColumn<OrderHistoryDTO, Timestamp> colDate;
    @FXML
    private TableColumn<OrderHistoryDTO, String> colProduct;
    @FXML
    private TableColumn<OrderHistoryDTO, Integer> colQuantity;
    @FXML
    private TableColumn<OrderHistoryDTO, Double> colPrice;
    @FXML
    private TableColumn<OrderHistoryDTO, Double> colTotal;

    private Runnable backHandler;
    private final UserDAO userDAO = new UserDAO();
    private final int userId = UserDTO.getInstance().getUserId();
    private final WishListService wishListService = new WishListServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserData();
        Platform.runLater(() -> {
            switchViewLogic(viewInfo, btnInfo);
        });
        btnBack.setOnAction(e -> {
            if (backHandler != null) backHandler.run();
        });
    }


    private void loadUserData() {
        User user = userDAO.findUserById(userId);
        UserDTO userSession = UserDTO.getInstance();

        if (user != null) {
            lblSidebarName.setText(user.getUsername());
            txtUsername.setText(user.getUsername());
            txtEmail.setText(user.getEmail());
            txtRole.setText(String.valueOf(user.getRole()));
            txtMoney.setText("Số dư: $" + String.format("%.2f", user.getMoney()));
        }


        if (imgAvatar != null) {
            String avatarUrl = userSession.getAvatarUrl();
            Image image = (avatarUrl != null && !avatarUrl.isEmpty())
                    ? new Image(avatarUrl, true)
                    : new Image(getClass().getResourceAsStream("/org/example/asset/avt.png")); // Đường dẫn ảnh mặc định của bạn

            image.progressProperty().addListener((obs, old, progress) -> {
                if (progress.doubleValue() == 1.0) {
                    // Center-Crop: Cắt ảnh từ giữa để tránh bị méo khi đưa vào khung vuông
                    double size = Math.min(image.getWidth(), image.getHeight());
                    double x = (image.getWidth() - size) / 2;
                    double y = (image.getHeight() - size) / 2;
                    imgAvatar.setViewport(new Rectangle2D(x, y, size, size));

                    // Tạo Clip bo góc 30px
                    Rectangle clip = new Rectangle(imgAvatar.getFitWidth(), imgAvatar.getFitHeight());
                    clip.setArcWidth(30);
                    clip.setArcHeight(30);
                    imgAvatar.setClip(clip);
                }
            });
            imgAvatar.setImage(image);
        }
    }


    private void switchViewLogic(VBox activeView, Button activeBtn) {
        VBox[] allViews = {viewInfo, viewHistory, viewDeposit, viewWishlist};
        Button[] allBtns = {btnInfo, btnHistory, btnDeposit, btnLike};

        for (VBox v : allViews) {
            if (v != null) {
                v.setVisible(false);
                v.setManaged(false);
            }
        }
        for (Button b : allBtns) {
            if (b != null) resetBtnStyle(b);
        }

        if (activeView != null) {
            activeView.setVisible(true);
            activeView.setManaged(true);
        }
        if (activeBtn != null) {
            activeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20; -fx-background-radius: 10;");
        }
    }

    private void resetBtnStyle(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-weight: normal; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20; -fx-cursor: hand;");
    }

    @FXML
    public void switchView(ActionEvent event) {
        Button clickedBtn = (Button) event.getSource();
        if (clickedBtn == btnInfo) switchViewLogic(viewInfo, btnInfo);
        else if (clickedBtn == btnHistory) {
            switchViewLogic(viewHistory, btnHistory);
            loadOrderHistory();
        } else if (clickedBtn == btnLike) {
            switchViewLogic(viewWishlist, btnLike);
            loadWishlistData();
        } else if (clickedBtn == btnDeposit) {
            switchViewLogic(viewDeposit, btnDeposit);
        }
    }

    @FXML
    public void handleDeposit(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txtDepositAmount.getText());
            if (amount <= 0) throw new NumberFormatException();

            userDAO.updateMoneyById(userId, amount);
            txtDepositAmount.clear();
            loadUserData();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Nạp tiền thành công!", ButtonType.OK);
            alert.show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Số tiền không hợp lệ!").show();
        }
    }

    private void loadOrderHistory() {
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        colDate.setCellFactory(column -> new TableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : format.format(item));
            }
        });

        List<OrderHistoryDTO> list = userDAO.getOrderHistoryById(userId);
        tblHistory.setItems(FXCollections.observableArrayList(list));
    }

    private void loadWishlistData() {
        wishlistContainer.getChildren().clear();
        List<WishListDTO> list = wishListService.getAllWishList(userId);
        for (WishListDTO item : list) {
            HBox itemBox = createWishlistItemBox(item);
            wishlistContainer.getChildren().add(itemBox);
        }
    }

    private HBox createWishlistItemBox(WishListDTO item) {
        HBox itemBox = new HBox(20);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setStyle("-fx-border-color: transparent transparent #eeeeee transparent; -fx-border-width: 0 0 1 0; -fx-padding: 15 0;");

        ImageView imageView = new ImageView(new Image(item.getImageUrl(), true));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        imageView.setPreserveRatio(true);

        Label lblTitle = new Label(item.getTitle());
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblTitle.setPrefWidth(250);

        Label lblPrice = new Label(String.format("$%.2f", item.getPrice()));
        lblPrice.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnDelete = new Button("Xóa");
        btnDelete.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            wishListService.deleteWishList(userId, item.getProductId());
            loadWishlistData();
        });

        itemBox.getChildren().addAll(imageView, lblTitle, lblPrice, spacer, btnDelete);
        return itemBox;
    }

    public void setOnBackAction(Runnable action) {
        this.backHandler = action;
    }
}