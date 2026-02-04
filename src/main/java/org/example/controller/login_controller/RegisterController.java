package org.example.controller.login_controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.User;
import org.example.model.dto.RegisterDTO;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;

import java.util.Set;

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
        RegisterDTO dto = new RegisterDTO(txtEmail.getText(), txtPassword.getText(),txtConfirmPassword.getText(),txtUsername.getText());
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            showAlert("Lỗi", violations.iterator().next().getMessage());
            return;
        }

        if (!password.equals(confirm)) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }

        boolean isSuccess = userService.register(username, password, email);

        if (isSuccess) {
            showAlert("Thành công", "Đăng ký tài khoản thành công!");
            User user = userService.searchUser(txtUsername.getText());
            UserDTO.login(user.getUserId(), user.getUsername());
            NavigationManager.switchScene(event, "UserView.fxml");
        } else {
            showAlert("Thất bại", "Tài khoản/Email đã tồn tại hoặc lỗi hệ thống!");
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