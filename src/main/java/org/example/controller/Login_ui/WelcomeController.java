package org.example.controller.Login_ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WelcomeController {

    @FXML
    private void handleLogin(ActionEvent event) {
        NavigationManager.switchScene(event, "LoginView.fxml");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Giả sử file đăng ký của bạn tên là RegisterView.fxml
        NavigationManager.switchScene(event, "RegisterView.fxml");
    }
}