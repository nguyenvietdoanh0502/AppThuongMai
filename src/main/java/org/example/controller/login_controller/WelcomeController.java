package org.example.controller.login_controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WelcomeController {

    @FXML
    private void handleLogin(ActionEvent event) {
        NavigationManager.switchScene(event, "LoginView.fxml");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        NavigationManager.switchScene(event, "RegisterView.fxml");
    }
}