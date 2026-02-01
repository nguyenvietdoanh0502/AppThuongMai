package org.example.controller.Login_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.service.UserService;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;

    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;

    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField txtConfirmPasswordVisible;

    @FXML private CheckBox checkShowPassword;

    private final UserService userService = new UserService();

    @FXML
    private void handleSignUp(ActionEvent event) {
        // Đồng bộ text từ ô Visible sang ô Password chính trước khi lưu
        if (checkShowPassword.isSelected()) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtConfirmPassword.setText(txtConfirmPasswordVisible.getText());
        }

        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirm = txtConfirmPassword.getText();
        String username = txtUsername.getText();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!password.equals(confirm)) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }

        boolean isSuccess = userService.register(username, password, email);

        if (isSuccess) {
            showAlert("Thành công", "Đăng ký tài khoản thành công!");
            NavigationManager.switchScene(event, "LoginView.fxml");
        } else {
            showAlert("Thất bại", "Tài khoản/Email đã tồn tại hoặc lỗi hệ thống!");
        }
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        boolean isShow = checkShowPassword.isSelected();

        // Hoán đổi cho ô mật khẩu
        updateVisibility(txtPassword, txtPasswordVisible, isShow);
        // Hoán đổi cho ô xác nhận
        updateVisibility(txtConfirmPassword, txtConfirmPasswordVisible, isShow);
    }

    private void updateVisibility(PasswordField pass, TextField text, boolean isShow) {
        if (isShow) {
            text.setText(pass.getText());
            text.setVisible(true);
            text.setManaged(true);
            pass.setVisible(false);
            pass.setManaged(false);
        } else {
            pass.setText(text.getText());
            pass.setVisible(true);
            pass.setManaged(true);
            text.setVisible(false);
            text.setManaged(false);
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        // Chú ý: Đã sửa tên file từ "Ưelcome" thành "Welcome"
        NavigationManager.switchScene(event, "WelcomeView.fxml");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}