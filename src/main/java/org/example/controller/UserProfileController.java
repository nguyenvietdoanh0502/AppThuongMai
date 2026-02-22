package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.model.dto.OrderHistoryDTO;
import org.example.model.dto.UserDTO;
import org.example.model.dto.WishListDTO;
import org.example.service.UserService;
import org.example.service.WishListService;
import org.example.service.impl.WishListServiceImpl;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {
    public Label lblSidebarName;
    public Button btnInfo;
    public Button btnHistory;
    public Button btnDeposit;
    public VBox viewInfo;
    public TextField txtEmail;
    public VBox viewHistory;
    public VBox viewDeposit;
    public TextField txtDepositAmount;
    public TextField txtUsername;
    public TextField txtRole;
    public TextField txtMoney;
    public Button btnBack;
    public Button btnLike;
    public VBox viewWishlist;
    public VBox wishlistContainer;
    @FXML private TableView<OrderHistoryDTO> tblHistory;
    @FXML private TableColumn<OrderHistoryDTO, Timestamp> colDate;
    @FXML private TableColumn<OrderHistoryDTO, String> colProduct;
    @FXML private TableColumn<OrderHistoryDTO, Integer> colQuantity;
    @FXML private TableColumn<OrderHistoryDTO, Double> colPrice;
    @FXML private TableColumn<OrderHistoryDTO, Double> colTotal;
    private Runnable backHandler;
    private UserDAO userDAO = new UserDAO();
    private int id = UserDTO.getInstance().getUserId();
    private WishListService wishListService = new WishListServiceImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUserData();
        switchViewLogic(viewInfo,btnInfo);
        btnBack.setOnAction(e->{
            if(backHandler!=null){
                backHandler.run();
            }
        });
    }
    private void loadUserData(){

        User user = userDAO.findUserById(id);
        lblSidebarName.setText(user.getUsername());
        txtRole.setText(String.valueOf(user.getRole()));
        txtEmail.setText(user.getEmail());
        txtMoney.setText(String.valueOf(user.getMoney()));
        txtUsername.setText(user.getUsername());
    }
    private void switchViewLogic(VBox activeView, Button activeBtn) {
        viewInfo.setVisible(false);
        viewHistory.setVisible(false);
        viewDeposit.setVisible(false);
        viewWishlist.setVisible(false);
        resetBtnStyle(btnInfo);
        resetBtnStyle(btnHistory);
        resetBtnStyle(btnDeposit);
        resetBtnStyle(btnLike);
        activeView.setVisible(true);
        activeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20;");
    }
    private void resetBtnStyle(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-weight: normal; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20; -fx-cursor: hand;");
    }

    public void switchView(javafx.event.ActionEvent event) {
        Button clickedBtn = (Button) event.getSource();
        if(clickedBtn == btnInfo){
            switchViewLogic(viewInfo,btnInfo);
        }
        else if(clickedBtn == btnHistory){
            switchViewLogic(viewHistory,btnHistory);
            loadOrderHistory();
        }
        else if(clickedBtn==btnLike){
            switchViewLogic(viewWishlist,btnLike);
            loadWishlistData();
        }
        else{
            switchViewLogic(viewDeposit,btnDeposit);
        }
    }

    public void handleDeposit(ActionEvent event) {
        try {
            double amount = Double.parseDouble(txtDepositAmount.getText());
            userDAO.updateMoneyById(id,amount);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Đã nạp " + amount + " vào tài khoản!");
            alert.show();
            txtDepositAmount.clear();
            loadUserData();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Vui lòng nhập số tiền hợp lệ!");
            alert.show();
        }
    }
    private void loadOrderHistory(){
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colDate.setCellFactory(column -> new TableCell<OrderHistoryDTO, Timestamp>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });
        List<OrderHistoryDTO> list = userDAO.getOrderHistoryById(id);
        tblHistory.setItems(FXCollections.observableArrayList(list));
    }
    public void setOnBackAction(Runnable action) {
        this.backHandler = action;
    }
    private void loadWishlistData() {
        wishlistContainer.getChildren().clear();
        List<WishListDTO> list = wishListService.getAllWishList(id);
        for (WishListDTO item : list) {
            HBox itemBox = new HBox(20);
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.setStyle("-fx-border-color: transparent transparent #eeeeee transparent; -fx-border-width: 0 0 1 0; -fx-padding: 15 0;");
            ImageView imageView = new ImageView();
            try {
                imageView.setImage(new Image(item.getImageUrl(), true));
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh: " + item.getImageUrl());
            }
            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
            imageView.setPreserveRatio(true);
            Label lblTitle = new Label(item.getTitle());
            lblTitle.setWrapText(true);
            lblTitle.setPrefWidth(250);
            lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
            Label lblPrice = new Label(String.format("$%.2f", item.getPrice()));
            lblPrice.setPrefWidth(80);
            lblPrice.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            SVGPath trashIcon = new SVGPath();
            trashIcon.setContent("M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
            trashIcon.setFill(Color.web("#ff4d4f"));
            trashIcon.setScaleY(1.3);
            Button btnDelete = new Button();
            btnDelete.setGraphic(trashIcon);
            btnDelete.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-cursor: hand; " +
                            "-fx-padding: 0 0 0 15;"
            );
            btnDelete.setOnMouseEntered(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #cf1322; -fx-font-size: 24px; -fx-cursor: hand; -fx-padding: 0 0 0 15;"));
            btnDelete.setOnMouseExited(e -> btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff4d4f; -fx-font-size: 24px; -fx-cursor: hand; -fx-padding: 0 0 0 15;"));
            btnDelete.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận xóa");
                alert.setHeaderText(null);
                alert.setContentText("Bạn có chắc chắn muốn xóa không?");
                ButtonType buttonTypeYes = new ButtonType("Có", ButtonBar.ButtonData.YES);
                ButtonType buttonTypeNo = new ButtonType("Không", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == buttonTypeYes) {
                    wishlistContainer.getChildren().remove(itemBox);
                    wishListService.deleteWishList(id,item.getProductId());
                }

            });
            itemBox.getChildren().addAll(imageView, lblTitle, lblPrice, spacer,btnDelete);
            wishlistContainer.getChildren().add(itemBox);
        }
    }
}
