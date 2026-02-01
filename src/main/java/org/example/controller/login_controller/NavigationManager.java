package org.example.controller.login_controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class NavigationManager {

    /**
     * Hàm dùng chung để chuyển đổi giữa các màn hình
     * @param event Sự kiện từ nút bấm để lấy Stage hiện tại
     * @param fxmlFile Tên file FXML muốn chuyển đến (vd: "LoginView.fxml")
     */
    public static void switchScene(ActionEvent event, String fxmlFile) {
        try {
            String path = "/view/" + fxmlFile;
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource(path));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            Scene scene = new Scene(root, width, height);

            stage.setScene(scene);
            root.setOpacity(0);
            javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.millis(500), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            stage.show();
        } catch (IOException e) {
            System.err.println("Lỗi load FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}