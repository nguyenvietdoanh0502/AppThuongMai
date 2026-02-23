package org.example.controller.login_controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.Event;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;

public class NavigationManager {
    public static String temporaryUsername = "";

    public static void switchScene(Event event, String fxmlFile) {
        try {
            // Đường dẫn đến file FXML của bạn
            String path = "/view/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(path));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            Scene scene = new Scene(root, width, height);

            stage.setScene(scene);

            root.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(500), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            stage.show();
        } catch (IOException e) {
            System.err.println("Lỗi load FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Không tìm thấy file FXML tại đường dẫn: /view/" + fxmlFile);
            e.printStackTrace();
        }
    }
}