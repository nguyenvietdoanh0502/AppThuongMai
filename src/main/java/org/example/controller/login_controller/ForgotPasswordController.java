package org.example.controller.login_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.constant.Animation;
import org.example.constant.Regex;
import org.example.service.UserService;

import java.util.regex.Pattern;

public class ForgotPasswordController {

    @FXML private VBox step1Box, step2Box;
    @FXML private Label lblMessage;
    @FXML private TextField txtUsername, txtEmail;
    @FXML private PasswordField txtNewPassword, txtConfirmPassword;

    private final UserService userService = new UserService();
    @FXML
    private void handleVerifyAccount(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();

        if(txtUsername.getText().isEmpty()){
            Animation.showAlert("Lỗi","Vui lòng điền đầy đủ Username!");
            return;
        }
        if(txtEmail.getText().isEmpty()){
            Animation.showAlert("Lỗi","Vui lòng điền đầy đủ Email!");
            return;
        }
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
    @FXML
    private void handleResetPassword(ActionEvent event) {
        String newPass = txtNewPassword.getText();
        String confirm = txtConfirmPassword.getText();
        String email = txtEmail.getText().trim();
        if(txtNewPassword.getText().isEmpty()){
            Animation.showAlert("Lỗi","Vui lòng điền đầy đủ Password!");
            return;
        }
        Pattern PASS_PATTERN = Pattern.compile(Regex.PASSWORD_PATTERN);
        if(!PASS_PATTERN.matcher(txtNewPassword.getText()).matches()){
            Animation.showAlert("Lỗi","Mật khẩu phải bao gồm chữ viết hoa, chữ viết thường, số, ký tự đặc biệt!");
            return;
        }
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