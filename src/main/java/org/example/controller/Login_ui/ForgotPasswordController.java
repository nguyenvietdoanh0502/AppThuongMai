package org.example.controller.Login_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.service.UserService;

public class ForgotPasswordController {

    @FXML private VBox step1Box, step2Box;
    @FXML private Label lblMessage;
    @FXML private TextField txtUsername, txtEmail;
    @FXML private PasswordField txtNewPassword, txtConfirmPassword;

    private final UserService userService = new UserService();

    // Bước 1: Khớp với onAction="#handleVerifyAccount" trong FXML
    @FXML
    private void handleVerifyAccount(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();

        if (userService.checkAccountExist(username, email)) {
            step1Box.setVisible(false);
            step1Box.setManaged(false);
            step2Box.setVisible(true);
            step2Box.setManaged(true);
            lblMessage.setText("Account verified! Please enter your new password.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Thông tin tài khoản không đúng!");
        }
    }

    // Bước 2: KHỚP CHÍNH XÁC với onAction="#handleResetPassword" trong FXML bạn gửi
    @FXML
    private void handleResetPassword(ActionEvent event) {
        String newPass = txtNewPassword.getText();
        String confirm = txtConfirmPassword.getText();
        String email = txtEmail.getText().trim();

        if (newPass.isEmpty() || !newPass.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Mật khẩu không khớp!");
            return;
        }

        if (userService.ResetPassword(email, newPass)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Mật khẩu đã được đổi!");
            NavigationManager.switchScene(event, "LoginView.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật mật khẩu thất bại!");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        NavigationManager.switchScene(event, "LoginView.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}