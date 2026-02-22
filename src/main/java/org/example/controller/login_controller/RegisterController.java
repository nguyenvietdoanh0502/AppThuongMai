package org.example.controller.login_controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.model.User;
import org.example.model.dto.RegisterDTO;
import org.example.model.dto.UserDTO;
import org.example.service.EmailService;
import org.example.service.UserService;

import java.util.Set;

public class RegisterController {

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtOTP;
    @FXML
    private Label lblCountdown; // Cần thêm vào FXML
    @FXML
    private Button btnSendOTP;  // Cần thêm vào FXML

    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private TextField txtConfirmPasswordVisible;
    @FXML
    private CheckBox checkShowPassword;

    private final UserService userService = new UserService();
    private String generatedOTP = null;
    private long otpCreationTime;
    private final long OTP_EXPIRY_DURATION = 60 * 1000; // 2 phút (mili giây)

    private Timeline timeline;
    private int secondsRemaining;

    @FXML
    private void handleSendOTP(ActionEvent event) {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập Email trước khi nhận mã!");
            return;
        }

        generatedOTP = String.format("%06d", (int) (Math.random() * 900000) + 100000);

        new Thread(() -> {
            try {
                EmailService.sendOTP(email, generatedOTP);
                otpCreationTime = System.currentTimeMillis();
                Platform.runLater(() -> {
                    showAlert("Thông báo", "Mã OTP đã được gửi đến: " + email);
                    startCountdown(60);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Lỗi", "Gửi mail thất bại!"));
                e.printStackTrace();
            }
        }).start();
    }

    private void startCountdown(int seconds) {
        secondsRemaining = seconds;
        btnSendOTP.setDisable(true);

        if (timeline != null) timeline.stop();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    secondsRemaining--;
                    int minutes = secondsRemaining / 60;
                    int secs = secondsRemaining % 60;
                    lblCountdown.setText(String.format("Mã hết hạn sau: %02d:%02d", minutes, secs));

                    if (secondsRemaining <= 0) {
                        timeline.stop();
                        lblCountdown.setText("Mã đã hết hạn!");
                        btnSendOTP.setDisable(false);
                        generatedOTP = null;
                    }
                })
        );
        timeline.play();
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        if (generatedOTP == null) {
            showAlert("Lỗi", "Mã OTP không tồn tại hoặc đã hết hạn!");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - otpCreationTime > OTP_EXPIRY_DURATION) {
            generatedOTP = null;
            showAlert("Lỗi", "Mã OTP đã hết hạn! Vui lòng nhấn gửi lại.");
            return;
        }

        String userEnteredOTP = txtOTP.getText().trim();
        if (!generatedOTP.equals(userEnteredOTP)) {
            showAlert("Lỗi", "Mã OTP không chính xác!");
            return;
        }
        if (checkShowPassword.isSelected()) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtConfirmPassword.setText(txtConfirmPasswordVisible.getText());
        }

        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirm = txtConfirmPassword.getText();
        String username = txtUsername.getText();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        RegisterDTO dto = new RegisterDTO(email, password, confirm, username);
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            showAlert("Lỗi", violations.iterator().next().getMessage());
            return;
        }

        if (!password.equals(confirm)) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }

        if (userService.register(username, password, email)) {
            if (timeline != null) timeline.stop();
            User user = userService.searchUser(txtUsername.getText());
            UserDTO.login(user.getUserId(), user.getUsername());
            showAlert("Thành công", "Đăng ký thành công!");
            NavigationManager.temporaryUsername = username;
            NavigationManager.switchScene(event, "LoginView.fxml");
        } else {
            showAlert("Thất bại", "Tài khoản/Email đã tồn tại!");
        }
    }


    @FXML
    private void togglePassword(ActionEvent event) {
        boolean isShow = checkShowPassword.isSelected();
        updateVisibility(txtPassword, txtPasswordVisible, isShow);
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
        if (timeline != null) timeline.stop();
        NavigationManager.switchScene(event, "WelcomeView.fxml");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}