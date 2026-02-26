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
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.model.User;
import org.example.model.dto.UserDTO;
import org.example.service.EmailService;
import org.example.service.UserService;

import java.awt.Desktop;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RegisterController {

    @FXML private TextField txtUsername, txtEmail, txtOTP, txtPasswordVisible, txtConfirmPasswordVisible;
    @FXML private PasswordField txtPassword, txtConfirmPassword;
    @FXML private Label lblCountdown;
    @FXML private Button btnSendOTP;
    @FXML private CheckBox checkShowPassword;

    private final UserService userService = new UserService();
    private String generatedOTP = null;

    // Cấu hình OAuth2
    private final String GOOGLE_CLIENT_ID = "137717395728-tjcpm6utt70ht57o2u1m2dcmb67g37lq.apps.googleusercontent.com";
    private final String GOOGLE_CLIENT_SECRET = "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";
    private final String FB_APP_ID = "1335495301926449";
    private final String FB_APP_SECRET = "a08d816fbf017cdaead625f6f7b9ec03";
    private final String REDIRECT_URI = "http://localhost:8888/";

    // --- 1. XỬ LÝ ĐĂNG KÝ/ĐĂNG NHẬP GOOGLE ---
    @FXML
    private void handleGoogleLogin(MouseEvent event) {
        new Thread(() -> {
            try {
                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET,
                        Arrays.asList("email", "profile")).setAccessType("offline").build();

                LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
                AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, receiver);
                Credential credential = app.authorize("user");

                PeopleService peopleService = new PeopleService.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Hobbee App").build();

                // Lấy thêm trường 'photos' để lấy avatar URL
                Person profile = peopleService.people().get("people/me")
                        .setPersonFields("names,emailAddresses,photos").execute();

                String fullName = profile.getNames().getFirst().getDisplayName();
                String email = profile.getEmailAddresses().getFirst().getValue();
                String avatarUrl = (profile.getPhotos() != null) ? profile.getPhotos().getFirst().getUrl() : "";

                Platform.runLater(() -> processSocialLogin(email, fullName, avatarUrl, "GOOGLE", event));
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Lỗi", "Kết nối Google thất bại!"));
            }
        }).start();
    }

    // --- 2. XỬ LÝ ĐĂNG KÝ/ĐĂNG NHẬP FACEBOOK ---
    @FXML
    private void handleFacebookLogin(MouseEvent event) {
        new Thread(() -> {
            try {
                String loginUrl = "https://www.facebook.com/v18.0/dialog/oauth?client_id=" + FB_APP_ID
                        + "&redirect_uri=" + REDIRECT_URI + "&scope=email,public_profile";
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(loginUrl));

                String authCode = listenForCode();
                if (authCode != null) {
                    FacebookClient.AccessToken token = new DefaultFacebookClient(Version.LATEST)
                            .obtainUserAccessToken(FB_APP_ID, FB_APP_SECRET, REDIRECT_URI, authCode);
                    FacebookClient fbClient = new DefaultFacebookClient(token.getAccessToken(), Version.LATEST);

                    // Lấy picture để lấy avatar URL
                    com.restfb.types.User fbUser = fbClient.fetchObject("me", com.restfb.types.User.class,
                            Parameter.with("fields", "name,email,picture.type(large)"));

                    String avatarUrl = (fbUser.getPicture() != null) ? fbUser.getPicture().getUrl() : "";

                    Platform.runLater(() -> processSocialLogin(fbUser.getEmail(), fbUser.getName(), avatarUrl, "FACEBOOK", event));
                }
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Lỗi", "Kết nối Facebook thất bại!"));
            }
        }).start();
    }

    // --- 3. HÀM XỬ LÝ CHUNG: TỰ ĐỘNG ĐĂNG KÝ & CHUYỂN TRANG ---
    private void processSocialLogin(String email, String fullName, String avatarUrl, String provider, MouseEvent event) {
        try {
            User user = userService.findByEmail(email);

            if (user == null) {
                String autoUsername = fullName.toLowerCase().replaceAll("\\s+", "") + (int)(Math.random()*100);
                String randomPass = UUID.randomUUID().toString().substring(0, 12);

                if (userService.register(autoUsername, randomPass, email)) {
                    user = userService.searchUser(autoUsername);
                }
            }

            if (user != null) {
                // CẬP NHẬT: Đã truyền đủ 3 tham số cho UserDTO
                UserDTO.login(user.getUserId(), user.getUsername(), avatarUrl);

                showAlert("Thành công", "Chào mừng " + fullName + "! Đăng nhập qua " + provider);
                NavigationManager.switchScene(event, "UserView.fxml");
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể xử lý đăng nhập mạng xã hội!");
        }
    }

    // --- 4. ĐĂNG KÝ THỦ CÔNG ---
    @FXML
    private void handleSignUp(ActionEvent event) {
        if (generatedOTP == null) { showAlert("Lỗi", "Vui lòng nhận mã OTP!"); return; }
        if (!generatedOTP.equals(txtOTP.getText().trim())) { showAlert("Lỗi", "Mã OTP không chính xác!"); return; }

        String email = txtEmail.getText(), password = txtPassword.getText(), username = txtUsername.getText();

        if (userService.register(username, password, email)) {
            User user = userService.searchUser(username);
            // Đăng ký thường -> Avatar tạm thời để trống ""
            UserDTO.login(user.getUserId(), user.getUsername(), "");
            showAlert("Thành công", "Đăng ký thành công!");
            NavigationManager.switchScene(event, "UserView.fxml");
        } else {
            showAlert("Thất bại", "Tài khoản hoặc Email đã tồn tại!");
        }
    }

    // --- 5. CÁC HÀM HỖ TRỢ ---
    private String listenForCode() throws Exception {
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(8888), 0);
        final String[] code = new String[1];
        CountDownLatch latch = new CountDownLatch(1);

        server.createContext("/", t -> {
            String query = t.getRequestURI().getQuery();
            if (query != null && query.contains("code=")) {
                code[0] = query.split("code=")[1].split("&")[0];
                String res = "Xac thuc thanh cong! Hay quay lai ung dung.";
                t.sendResponseHeaders(200, res.length());
                t.getResponseBody().write(res.getBytes()); t.getResponseBody().close();
                latch.countDown();
                server.stop(0);
            }
        });
        server.start();
        latch.await(60, TimeUnit.SECONDS);
        return code[0];
    }

    @FXML private void handleSendOTP(ActionEvent event) {
        String email = txtEmail.getText().trim();
        if (email.isEmpty()) { showAlert("Lỗi", "Vui lòng nhập Email!"); return; }
        generatedOTP = String.format("%06d", (int) (Math.random() * 900000) + 100000);
        new Thread(() -> {
            try {
                EmailService.sendOTP(email, generatedOTP);
                Platform.runLater(() -> showAlert("Thông báo", "Mã OTP đã gửi đến " + email));
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    @FXML private void handleBackToLogin(ActionEvent event) { NavigationManager.switchScene(event, "LoginView.fxml"); }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.show();
    }

    @FXML
    private void togglePassword(ActionEvent event) {
        boolean isShow = checkShowPassword.isSelected();
        txtPasswordVisible.setText(txtPassword.getText());
        txtPassword.setText(txtPasswordVisible.getText());

        txtPasswordVisible.setVisible(isShow); txtPasswordVisible.setManaged(isShow);
        txtPassword.setVisible(!isShow); txtPassword.setManaged(!isShow);
    }
}