package org.example.controller.Login_ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.service.UserService;
import org.example.service.EmailService;

public class ForgotPasswordController {

    @FXML
    private VBox step1Box, step2Box, otpBox;
    @FXML
    private Label lblMessage;
    @FXML
    private TextField txtUsername, txtEmail, txtOTPInput;

    // Các ô mật khẩu (Ẩn và Hiện)
    @FXML
    private PasswordField txtNewPassword, txtConfirmPassword;
    @FXML
    private TextField txtNewPasswordVisible, txtConfirmPasswordVisible;
    @FXML
    private CheckBox checkShowPassword;

    private final UserService userService = new UserService();
    private String generatedOTP;

    @FXML
    private void handleSendOTP(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();

        if (username.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (userService.checkAccountExist(username, email)) {
            generatedOTP = String.format("%06d", new java.util.Random().nextInt(999999));
            lblMessage.setText("Sending OTP to your email...");

            new Thread(() -> {
                try {
                    EmailService.sendOTP(email, generatedOTP);
                    Platform.runLater(() -> {
                        lblMessage.setText("OTP Sent! Please check your email.");
                        showOTPStep();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể gửi mail!"));
                }
            }).start();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Thông tin tài khoản không đúng!");
        }
    }

    @FXML
    private void handleVerifyOTP(ActionEvent event) {
        if (txtOTPInput.getText().trim().equals(generatedOTP)) {
            otpBox.setVisible(false);
            otpBox.setManaged(false);
            step2Box.setVisible(true);
            step2Box.setManaged(true);
            lblMessage.setText("OTP Verified! Set your new password.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã OTP không chính xác!");
        }
    }

    // --- LOGIC HIỂN THỊ MẬT KHẨU ---
    @FXML
    private void handleTogglePassword(ActionEvent event) {
        if (checkShowPassword.isSelected()) {
            // Đồng bộ text sang ô TextField (Hiện)
            txtNewPasswordVisible.setText(txtNewPassword.getText());
            txtConfirmPasswordVisible.setText(txtConfirmPassword.getText());

            showFields(true);
        } else {
            txtNewPassword.setText(txtNewPasswordVisible.getText());
            txtConfirmPassword.setText(txtConfirmPasswordVisible.getText());

            showFields(false);
        }
    }

    private void showFields(boolean isVisible) {
        txtNewPasswordVisible.setVisible(isVisible);
        txtNewPasswordVisible.setManaged(isVisible);
        txtNewPassword.setVisible(!isVisible);
        txtNewPassword.setManaged(!isVisible);

        txtConfirmPasswordVisible.setVisible(isVisible);
        txtConfirmPasswordVisible.setManaged(isVisible);
        txtConfirmPassword.setVisible(!isVisible);
        txtConfirmPassword.setManaged(!isVisible);
    }

    @FXML
    private void handleResetPassword(ActionEvent event) {
        // Lấy giá trị từ ô đang hiển thị
        String newPass = checkShowPassword.isSelected() ? txtNewPasswordVisible.getText() : txtNewPassword.getText();
        String confirm = checkShowPassword.isSelected() ? txtConfirmPasswordVisible.getText() : txtConfirmPassword.getText();
        String email = txtEmail.getText().trim();

        if (newPass.isEmpty() || !newPass.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Mật khẩu không khớp hoặc trống!");
            return;
        }

        if (userService.ResetPassword(email, newPass)) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Mật khẩu đã được đổi!");
            NavigationManager.switchScene(event, "LoginView.fxml");
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật mật khẩu thất bại!");
        }
    }

    private void showOTPStep() {
        step1Box.setVisible(false);
        step1Box.setManaged(false);
        if (otpBox != null) {
            otpBox.setVisible(true);
            otpBox.setManaged(true);
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