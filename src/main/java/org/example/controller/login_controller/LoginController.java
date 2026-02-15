package org.example.controller.login_controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.example.model.User;
import org.example.model.dto.UserDTO;
import org.example.service.UserService;
import org.example.model.Status;
import org.example.controller.login_controller.NavigationManager;

import java.awt.Desktop;
import java.net.URI;
import java.util.Arrays;

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

    // Google Config
    private final String GOOGLE_CLIENT_ID = "137717395728-tjcpm6utt70ht57o2u1m2dcmb67g37lq.apps.googleusercontent.com";
    private final String GOOGLE_CLIENT_SECRET = "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";

    // Facebook Config
    private final String FB_APP_ID = "1335495301926449";
    private final String FB_APP_SECRET = "a08d816fbf017cdaead625f6f7b9ec03";
    private final String REDIRECT_URI = "http://localhost:8888/";

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = checkShowPassword.isSelected() ? txtPasswordVisible.getText() : txtPassword.getText();

        if (username.trim().isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ!");
            return;
        }

        try {
            User loggedInUser = userService.login(username, password);
            if (loggedInUser != null) {
                completeLogin(loggedInUser, loggedInUser.getUsername(), event);
            } else {
                showAlert(Alert.AlertType.ERROR, "Đăng nhập thất bại", "Tài khoản hoặc mật khẩu không chính xác!");
            }
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.WARNING, "Tài khoản bị khóa", e.getMessage());
        }
    }

    private void processSocialLogin(String email, String fullName, String provider, ActionEvent event) {
        try {
            User user = userService.findByEmail(email);

            if (user == null) {
                user = userService.createSocialUser(email, fullName, provider);
            } else {
                System.out.println("User đã tồn tại, tiến hành đăng nhập vào tài khoản hiện có.");
            }

            if (user != null) {
                if (user.getStatus() == Status.LOCKED) {
                    showAlert(Alert.AlertType.WARNING, "Truy cập bị từ chối",
                            "Tài khoản này hiện đang bị khóa.");
                    return;
                }
                completeLogin(user, fullName, event);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xử lý đăng nhập " + provider);
        }
    }

    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        new Thread(() -> {
            try {
                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        GOOGLE_CLIENT_ID,
                        GOOGLE_CLIENT_SECRET,
                        Arrays.asList("email", "profile"))
                        .setAccessType("offline")
                        .build();

                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
                AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, receiver);
                Credential credential = app.authorize("user");

                PeopleService peopleService = new PeopleService.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        credential)
                        .setApplicationName("Hobbee App")
                        .build();

                Person profile = peopleService.people().get("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();

                String fullName = profile.getNames().get(0).getDisplayName();
                String email = profile.getEmailAddresses().get(0).getValue();

                // ĐÃ CẬP NHẬT: Truyền đủ 4 tham số (Email, Name, "GOOGLE", event)
                Platform.runLater(() -> processSocialLogin(email, fullName, "GOOGLE", event));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng nhập Google thất bại!"));
            }
        }).start();
    }

    @FXML
    private void handleFacebookLogin(ActionEvent event) {
        new Thread(() -> {
            try {
                String loginUrl = "https://www.facebook.com/v18.0/dialog/oauth?"
                        + "client_id=" + FB_APP_ID
                        + "&redirect_uri=" + REDIRECT_URI
                        + "&scope=email,public_profile";

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(loginUrl));
                }

                String authCode = listenForCode();

                if (authCode != null) {
                    FacebookClient.AccessToken token = new DefaultFacebookClient(Version.LATEST)
                            .obtainUserAccessToken(FB_APP_ID, FB_APP_SECRET, REDIRECT_URI, authCode);

                    FacebookClient fbClient = new DefaultFacebookClient(token.getAccessToken(), Version.LATEST);
                    com.restfb.types.User fbUser = fbClient.fetchObject("me", com.restfb.types.User.class,
                            Parameter.with("fields", "name,email"));

                    // ĐÃ CẬP NHẬT: Truyền đủ 4 tham số (Email, Name, "FACEBOOK", event)
                    Platform.runLater(() -> processSocialLogin(fbUser.getEmail(), fbUser.getName(), "FACEBOOK", event));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Lỗi", "Đăng nhập Facebook thất bại!"));
            }
        }).start();
    }

    private String listenForCode() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(8888), 0);
        final String[] code = new String[1];
        server.createContext("/", t -> {
            String query = t.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                code[0] = query.split("code=")[1].split("&")[0];
                String res = "Xac thuc thanh cong! Hay quay lai ung dung.";
                t.sendResponseHeaders(200, res.length());
                t.getResponseBody().write(res.getBytes());
                t.getResponseBody().close();
                server.stop(0);
            }
        });
        server.start();
        long start = System.currentTimeMillis();
        while (code[0] == null && System.currentTimeMillis() - start < 60000) {
            Thread.sleep(500);
        }
        return code[0];
    }

    private void completeLogin(User user, String displayName, ActionEvent event) {
        UserDTO.login(user.getUserId(), user.getUsername());
        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Chào mừng " + displayName);
        String roleName = String.valueOf(user.getRole());
        String view = "ADMIN".equalsIgnoreCase(roleName) ? "AdminView.fxml" : "UserView.fxml";
        NavigationManager.switchScene(event, view);
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
    public void initialize() {
        if (NavigationManager.temporaryUsername != null && !NavigationManager.temporaryUsername.isEmpty()) {
            txtUsername.setText(NavigationManager.temporaryUsername);
            NavigationManager.temporaryUsername = "";
            Platform.runLater(() -> txtPassword.requestFocus());
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