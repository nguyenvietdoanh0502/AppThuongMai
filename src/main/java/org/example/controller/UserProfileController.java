package org.example.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.model.dto.OrderHistoryDTO;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
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
    @FXML private TableView<OrderHistoryDTO> tblHistory;
    @FXML private TableColumn<OrderHistoryDTO, Timestamp> colDate;
    @FXML private TableColumn<OrderHistoryDTO, String> colProduct;
    @FXML private TableColumn<OrderHistoryDTO, Integer> colQuantity;
    @FXML private TableColumn<OrderHistoryDTO, Double> colPrice;
    @FXML private TableColumn<OrderHistoryDTO, Double> colTotal;
    private Runnable backHandler;
    private UserDAO userDAO = new UserDAO();
    private int id = UserDTO.getInstance().getUserId();
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
        resetBtnStyle(btnInfo);
        resetBtnStyle(btnHistory);
        resetBtnStyle(btnDeposit);
        activeView.setVisible(true);
        activeBtn.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-weight: bold; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20;");
    }
    private void resetBtnStyle(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555555; -fx-font-weight: normal; -fx-alignment: BASELINE_LEFT; -fx-padding: 10 20;");
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
}
