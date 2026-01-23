package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.UserService;

import java.io.IOException;
import java.util.EventObject;

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
        // Đồng bộ dữ liệu giữa ô mật khẩu ẩn và hiện
        if (txtPasswordVisible != null && txtPassword != null) {
            txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

    // --- CÁC PHƯƠNG THỨC DÀNH CHO MAINLOGIN (FIX LỖI CANNOT RESOLVE) ---

    public String register(String u, String p, String e) {
        return userService.register(u, p, e) ? "Thành công" : "Thất bại";
    }

    public User login(String u, String p) {
        return userService.login(u, p);
    }

    public void RoleAssignment(User user) {
        if (user != null) {
            System.out.println("Quyền người dùng: " + user.getRole());
        }
    }

    public String resetPasswordLogic(String email, String newPass) {
        return userService.ResetPassword(email, newPass) ? "Đổi mật khẩu thành công" : "Lỗi thực thi";
    }

    public boolean checkAccount(String u, String e) {
        return userService.checkAccountExist(u, e);
    }

    // --- XỬ LÝ GIAO DIỆN JAVAFX ---

    @FXML
    public void showMenu() {
        toggleViews(true);
        lblTitle.setText("Mua Sắm Ngay Thôi");
        clearFields();
        isVerifyingStep = true;
    }

    @FXML
    public void showLoginFrame() {
        isRegisterMode = false;
        toggleViews(false);
        lblTitle.setText("ĐĂNG NHẬP");
        setFieldsVisibility(false, false);
        btnSubmit.setText("Đăng nhập");
        linkForgot.setVisible(true);
        linkForgot.setManaged(true);
    }

    @FXML
    public void showRegisterFrame() {
        isRegisterMode = true;
        toggleViews(false);
        lblTitle.setText("ĐĂNG KÝ");
        setFieldsVisibility(true, true);
        btnSubmit.setText("Tạo tài khoản");
        linkForgot.setVisible(false);
        linkForgot.setManaged(false);
    }

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




    private void handleLoginInternal() {
        User user = login(txtUsername.getText(), txtPassword.getText());
        if (user != null) {
            showAlert("Thành công", "Đăng nhập thành công!");
            RoleAssignment(user);
            switchToMainView();

        } else {
            showAlert("Lỗi", "Sai tài khoản hoặc mật khẩu!");
        }
    }

    private void handleRegisterInternal() {
        if (!txtPassword.getText().equals(txtConfirm.getText())) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }
        String res = register(txtUsername.getText(), txtPassword.getText(), txtEmail.getText());
        if (res.equals("Thành công")) {
            showAlert("Thành công", "Đăng ký thành công!");
            showMenu();
        } else {
            showAlert("Lỗi", "Tài khoản đã tồn tại!");
        }
    }

    @FXML
    public void handleForgotPassword() {
        if (!lblTitle.getText().equals("KHÔI PHỤC MẬT KHẨU")) {
            setupForgotUI();
            return;
        }

        if (isVerifyingStep) {
            if (checkAccount(txtUsername.getText(), txtEmail.getText())) {
                showAlert("Thành công", "username và email hợp lệ! Nhập mật khẩu mới.");
                showStep2UI();
            } else {
                showAlert("Lỗi", "Username hoặc Email không chính xác!");
            }
        } else {
            String res = resetPasswordLogic(txtEmail.getText(), txtPassword.getText());
            showAlert("Thông báo", res);
            if (res.contains("thành công")) showMenu();
        }
    }

    // --- TRỢ GIÚP GIAO DIỆN ---

    private void setupForgotUI() {
        lblTitle.setText("KHÔI PHỤC MẬT KHẨU");
        isVerifyingStep = true;
        toggleViews(false);
        clearFields();

        txtUsername.setVisible(true);   txtUsername.setManaged(true);
        txtEmail.setVisible(true);      txtEmail.setManaged(true);

        txtPassword.setVisible(false);         txtPassword.setManaged(false);
        txtPasswordVisible.setVisible(false);  txtPasswordVisible.setManaged(false);
        txtConfirm.setVisible(false);          txtConfirm.setManaged(false);
        checkShowPassword.setVisible(false);    checkShowPassword.setManaged(false);

        btnSubmit.setText("Kiểm tra thông tin");
    }

    private void showStep2UI() {
        isVerifyingStep = false;
        txtPassword.clear();
        txtConfirm.clear();

        txtUsername.setDisable(true);
        txtEmail.setDisable(true);

        txtPassword.setVisible(true);   txtPassword.setManaged(true);
        txtConfirm.setVisible(true);    txtConfirm.setManaged(true);
        checkShowPassword.setVisible(true); checkShowPassword.setManaged(true);

        btnSubmit.setText("Cập nhật mật khẩu");
    }

    @FXML
    public void handleShowPassword() {
        boolean isSelected = checkShowPassword.isSelected();
        txtPassword.setVisible(!isSelected);
        txtPassword.setManaged(!isSelected);
        txtPasswordVisible.setVisible(isSelected);
        txtPasswordVisible.setManaged(isSelected);
    }

    private void toggleViews(boolean showMenu) {
        paneChoice.setVisible(showMenu);   paneChoice.setManaged(showMenu);
        paneFields.setVisible(!showMenu);  paneFields.setManaged(!showMenu);
    }

    private void setFieldsVisibility(boolean showConfirm, boolean showEmail) {
        txtConfirm.setVisible(showConfirm); txtConfirm.setManaged(showConfirm);
        txtEmail.setVisible(showEmail);     txtEmail.setManaged(showEmail);
        txtPassword.setVisible(true);       txtPassword.setManaged(true);
        checkShowPassword.setVisible(true); checkShowPassword.setManaged(true);
    }

    private void clearFields() {
        txtUsername.clear(); txtUsername.setDisable(false);
        txtEmail.clear();    txtEmail.setDisable(false);
        txtPassword.clear();
        txtConfirm.clear();
        if (checkShowPassword != null) {
            checkShowPassword.setSelected(false);
            handleShowPassword();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void switchToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            Scene scene = new Scene(root,1100,700);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi hệ thống", "Không tìm thấy file giao diện UserView.fxml");
        }
    }
}