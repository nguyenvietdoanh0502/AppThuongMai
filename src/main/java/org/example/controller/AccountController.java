package org.example.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;

import org.example.constant.Regex;
import org.example.model.Role;
import org.example.model.User;
import org.example.model.dto.RegisterDTO;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;
import jakarta.validation.Validator;

import java.io.IOException;
import java.util.EventObject;
import java.util.Set;
import java.util.regex.Pattern;

public class AccountController {
    @FXML private HBox paneChoice;
    @FXML private VBox paneFields;
    @FXML private Label lblTitle;
    @FXML private Button btnSubmit;
    @FXML private Hyperlink linkForgot;
    @FXML private TextField txtUsername, txtEmail;
    @FXML private PasswordField txtPassword, txtConfirm;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox checkShowPassword;

    private final UserService userService = new UserService();
    private boolean isRegisterMode = false;
    private boolean isVerifyingStep = true;

    @FXML
    public void initialize() {
        if (txtPasswordVisible != null && txtPassword != null) {
            txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

    // --- XỬ LÝ ĐĂNG NHẬP ---
    private void handleLoginInternal() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        User user = userService.login(username, password);

        if (user != null) {
            // CẬP NHẬT: Thêm tham số thứ 3 là "" (chuỗi rỗng) cho AvatarUrl
            // Nếu sau này bạn lưu avatar trong DB, hãy thay bằng user.getAvatarUrl()
            UserDTO.login(user.getUserId(), user.getUsername(), "");

            if (user.getRole() == Role.ADMIN) {
                showAlert("Thành công", "Chào mừng Admin quay trở lại!");
                switchToAdminView();
            } else {
                showAlert("Thành công", "Đăng nhập thành công!");
                switchToUserView();
            }
        } else {
            showAlert("Lỗi", "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }

    // --- XỬ LÝ ĐĂNG KÝ ---
    private void handleRegisterInternal() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        RegisterDTO dto = new RegisterDTO(txtEmail.getText(), txtPassword.getText(), txtConfirm.getText(), txtUsername.getText());
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            showAlert("Lỗi", violations.iterator().next().getMessage());
            return;
        }
        if (!txtPassword.getText().equals(txtConfirm.getText())) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }

        String res = userService.register(txtUsername.getText(), txtPassword.getText(), txtEmail.getText()) ? "Thành công" : "Thất bại";

        if (res.equals("Thành công")) {
            showAlert("Thành công", "Đăng ký thành công!");
            User user = userService.searchUser(txtUsername.getText());

            // CẬP NHẬT: Thêm tham số thứ 3 là "" cho AvatarUrl
            UserDTO.login(user.getUserId(), user.getUsername(), "");

            switchToUserView();
        } else {
            showAlert("Lỗi", "Tài khoản đã tồn tại!");
        }
    }

    // --- CÁC PHƯƠNG THỨC HỖ TRỢ ---
    @FXML
    public void handleSubmit() {
        if (lblTitle.getText().equals("KHÔI PHỤC MẬT KHẨU")) {
            handleForgotPassword();
        } else if (isRegisterMode) {
            handleRegisterInternal();
        } else {
            handleLoginInternal();
        }
    }

    @FXML
    public void handleForgotPassword() {
        if (!lblTitle.getText().equals("KHÔI PHỤC MẬT KHẨU")) {
            setupForgotUI();
            return;
        }
        if (txtUsername.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (isVerifyingStep) {
            if (userService.checkAccountExist(txtUsername.getText(), txtEmail.getText())) {
                showAlert("Thành công", "Thông tin hợp lệ! Mời nhập mật khẩu mới.");
                showStep2UI();
            } else {
                showAlert("Lỗi", "Username hoặc Email không chính xác!");
            }
        } else {
            if (txtPassword.getText().isEmpty() || !txtPassword.getText().equals(txtConfirm.getText())) {
                showAlert("Lỗi", "Mật khẩu không khớp hoặc để trống!");
                return;
            }
            Pattern PASS_PATTERN = Pattern.compile(Regex.PASSWORD_PATTERN);
            if (!PASS_PATTERN.matcher(txtPassword.getText()).matches()) {
                showAlert("Lỗi", "Mật khẩu phải bao gồm hoa, thường, số, ký tự đặc biệt!");
                return;
            }

            if (userService.ResetPassword(txtEmail.getText(), txtPassword.getText())) {
                showAlert("Thông báo", "Đổi mật khẩu thành công!");
                showMenu();
            } else {
                showAlert("Lỗi", "Lỗi thực thi!");
            }
        }
    }

    // --- UI LOGIC (GIỮ NGUYÊN) ---
    private void setupForgotUI() {
        lblTitle.setText("KHÔI PHỤC MẬT KHẨU");
        isVerifyingStep = true;
        toggleViews(false);
        clearFields();
        txtUsername.setVisible(true); txtUsername.setManaged(true);
        txtEmail.setVisible(true); txtEmail.setManaged(true);
        txtPassword.setVisible(false); txtPassword.setManaged(false);
        txtConfirm.setVisible(false); txtConfirm.setManaged(false);
        btnSubmit.setText("Kiểm tra thông tin");
    }

    private void showStep2UI() {
        isVerifyingStep = false;
        txtPassword.setVisible(true); txtPassword.setManaged(true);
        txtConfirm.setVisible(true); txtConfirm.setManaged(true);
        btnSubmit.setText("Cập nhật mật khẩu");
    }

    @FXML public void showMenu() { toggleViews(true); lblTitle.setText("Mua Sắm Ngay Thôi"); clearFields(); }
    @FXML public void showLoginFrame() { isRegisterMode = false; toggleViews(false); lblTitle.setText("ĐĂNG NHẬP"); setFieldsVisibility(false, false); btnSubmit.setText("Đăng nhập"); }
    @FXML public void showRegisterFrame() { isRegisterMode = true; toggleViews(false); lblTitle.setText("ĐĂNG KÝ"); setFieldsVisibility(true, true); btnSubmit.setText("Tạo tài khoản"); }

    private void toggleViews(boolean showMenu) {
        paneChoice.setVisible(showMenu); paneChoice.setManaged(showMenu);
        paneFields.setVisible(!showMenu); paneFields.setManaged(!showMenu);
    }

    private void setFieldsVisibility(boolean showConfirm, boolean showEmail) {
        txtConfirm.setVisible(showConfirm); txtConfirm.setManaged(showConfirm);
        txtEmail.setVisible(showEmail); txtEmail.setManaged(showEmail);
        linkForgot.setVisible(!isRegisterMode);
    }

    private void clearFields() {
        txtUsername.clear(); txtEmail.clear(); txtPassword.clear(); txtConfirm.clear();
        txtUsername.setDisable(false); txtEmail.setDisable(false);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }

    private void switchToUserView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/UserView.fxml"));
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.centerOnScreen();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void switchToAdminView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminView.fxml"));
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.centerOnScreen();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void handleShowPassword() {
        boolean isSelected = checkShowPassword.isSelected();
        txtPassword.setVisible(!isSelected);
        txtPassword.setManaged(!isSelected);
        txtPasswordVisible.setVisible(isSelected);
        txtPasswordVisible.setManaged(isSelected);
    }
}