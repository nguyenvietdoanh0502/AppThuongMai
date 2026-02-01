package org.example.controller.Login_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.service.UserService;
import org.example.model.User; // Import model User để hứng dữ liệu trả về

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private CheckBox checkShowPassword;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = checkShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();

        if (username.trim().isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ!");
            return;
        }

        User loggedInUser = userService.login(username, password);

        if (loggedInUser != null) {
            System.out.println("Đăng nhập thành công! Vai trò: " + loggedInUser.getRole());

            // KIỂM TRA ROLE ĐỂ CHUYỂN TRANG ĐÚNG
            if ("ADMIN".equalsIgnoreCase(String.valueOf(loggedInUser.getRole()))) {
                System.out.println("Đang chuyển hướng tới giao diện Quản trị...");
                NavigationManager.switchScene(event, "AdminView.fxml");
            } else {
                System.out.println("Đang chuyển hướng tới giao diện Người dùng...");
                NavigationManager.switchScene(event, "UserView.fxml");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        if (checkShowPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        NavigationManager.switchScene(event, "WelcomeView.fxml");
    }

    @FXML
    private void handleForgot(ActionEvent event) {
        NavigationManager.switchScene(event, "ForgotPasswordView.fxml");
    }

    // Hàm hiển thị Alert hỗ trợ xử lý lỗi Alert đỏ
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}