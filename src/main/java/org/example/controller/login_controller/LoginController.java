package org.example.controller.login_controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.model.User;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;

import java.util.Arrays;
import java.util.Collections;

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private CheckBox checkShowPassword;
    @FXML
    private ImageView imgAvatar;

    private final UserService userService = new UserService();

    // Thông tin từ file JSON bạn cung cấp
    private final String GOOGLE_CLIENT_ID = "137717395728-tjcpm6utt70ht57o2u1m2dcmb67g37lq.apps.googleusercontent.com";
    private final String GOOGLE_CLIENT_SECRET = "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";

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
            UserDTO.login(loggedInUser.getUserId(), loggedInUser.getUsername());
            if ("ADMIN".equalsIgnoreCase(String.valueOf(loggedInUser.getRole()))) {
                NavigationManager.switchScene(event, "AdminView.fxml");
            } else {
                NavigationManager.switchScene(event, "UserView.fxml");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tài khoản hoặc mật khẩu không chính xác!");
        }
    }

    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        new Thread(() -> {
            try {
                // 1. Cấu hình luồng OAuth2
                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        GOOGLE_CLIENT_ID,
                        GOOGLE_CLIENT_SECRET,
                        Arrays.asList("email", "profile"))
                        .setAccessType("offline")
                        .build();
                // 2. Mở trình duyệt xác thực
                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
                AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, receiver);
                Credential credential = app.authorize("user");
                // 3. Gọi People API để lấy thông tin cá nhân
                PeopleService peopleService = new PeopleService.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        credential)
                        .setApplicationName("Hobbee App")
                        .build();
                Person profile = peopleService.people().get("people/me")
                        .setPersonFields("names,emailAddresses,photos")
                        .execute();
                String fullName = profile.getNames().get(0).getDisplayName();
                String email = profile.getEmailAddresses().get(0).getValue();
                String avatarUrl = (profile.getPhotos() != null && !profile.getPhotos().isEmpty())
                        ? profile.getPhotos().get(0).getUrl() : null;
                // 4. Xử lý logic Đăng nhập/Đăng ký trên UI Thread
                Platform.runLater(() -> {
                    User loggedInUser = userService.findByEmail(email);

                    if (loggedInUser == null) {
                        loggedInUser = userService.createGoogleUser(email, fullName);
                    }
                    if (loggedInUser != null) {
                        UserDTO.login(loggedInUser.getUserId(), loggedInUser.getUsername());
                        // THÔNG BÁO THÀNH CÔNG
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Thành công");
                        alert.setHeaderText(null);
                        alert.setContentText("Chào mừng " + fullName);
                        // showAndWait sẽ tạm dừng luồng cho đến khi bạn bấm OK
                        alert.showAndWait();
                        // CHUYỂN MÀN HÌNH (Sử dụng biến event từ tham số hàm)
                        if ("ADMIN".equalsIgnoreCase(String.valueOf(loggedInUser.getRole()))) {
                            NavigationManager.switchScene(event, "AdminView.fxml");
                        } else {
                            NavigationManager.switchScene(event, "UserView.fxml");
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng nhập thất bại!"));
            }
        }).start();
    }

    @FXML
    private void handleFacebookLogin(ActionEvent event) {
        System.out.println("Chức năng Facebook đang được tích hợp...");
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}