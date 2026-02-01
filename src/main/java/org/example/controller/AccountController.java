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
        if(txtUsername.getText().isEmpty()){
            showAlert("Lỗi","Vui lòng điền đầy đủ Username!");
            return;
        }
        if(txtPassword.getText().isEmpty()){
            showAlert("Lỗi","Vui lòng điển đầy đủ Password!");
            return;
        }
        User user = login(txtUsername.getText(), txtPassword.getText());
        if (user != null) {
//            showAlert("Thành công", "Đăng nhập thành công!");
            RoleAssignment(user);

            UserDTO.login(user.getUserId(), user.getUsername());
            if(user.getRole()== Role.USER){
                switchToUserView();
            }
            else{
                switchToAdminView();
            }

        } else {
            showAlert("Lỗi", "Sai tài khoản hoặc mật khẩu!");
        }
    }

    private void handleRegisterInternal() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        RegisterDTO dto = new RegisterDTO(txtEmail.getText(), txtPassword.getText(),txtConfirm.getText(),txtUsername.getText());
        Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            showAlert("Lỗi", violations.iterator().next().getMessage());
            return;
        }
        if (!txtPassword.getText().equals(txtConfirm.getText())) {
            showAlert("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }
        String res = register(txtUsername.getText(), txtPassword.getText(), txtEmail.getText());
        if (res.equals("Thành công")) {
            showAlert("Thành công", "Đăng ký thành công!");
            User user = userService.searchUser(txtUsername.getText());
            UserDTO.login(user.getUserId(), user.getUsername());
            switchToUserView();

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
        if(txtUsername.getText().isEmpty()){
            showAlert("Lỗi","Vui lòng điền đầy đủ Username!");
            return;
        }
        if(txtEmail.getText().isEmpty()){
            showAlert("Lỗi","Vui lòng điền đầy đủ Password!");
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
            if(txtPassword.getText().isEmpty()){
                showAlert("Lỗi","Vui lòng điền đầy đủ Password!");
                return;
            }
            Pattern PASS_PATTERN = Pattern.compile(Regex.PASSWORD_PATTERN);
            if(!PASS_PATTERN.matcher(txtPassword.getText()).matches()){
                showAlert("Lỗi","Mật khẩu phải bao gồm chữ viết hoa, chữ viết thường, số, ký tự đặc biệt!");
                return;
            }
            if(!txtPassword.getText().equals(txtConfirm.getText())){
                showAlert("Lỗi","Xác nhận mật khẩu không trùng khớp!");
                return;
            }

            String res = resetPasswordLogic(txtEmail.getText(), txtPassword.getText());
            showAlert("Thông báo", res);
            if (res.contains("thành công")) showMenu();
        }
    }


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
        linkForgot.setVisible(false);
        linkForgot.setManaged(false);
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
    private void switchToUserView() {
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
    }private void switchToAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminView.fxml"));
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