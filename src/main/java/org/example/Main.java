package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.api.SaveDataFromAPI;
import org.example.service.UserService; // Thêm import này

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        UserService userService = new UserService();
        userService.initDefaultAdmin();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/DashboardView.fxml")
        );
        Scene scene = new Scene(loader.load(), 1050, 700);
        stage.setScene(scene);
        stage.setTitle("Hệ thống bán hàng");
        stage.show();

        new Thread(() -> {
            try {
                SaveDataFromAPI e = new SaveDataFromAPI();
                e.saveOrUpdateFromApi();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}