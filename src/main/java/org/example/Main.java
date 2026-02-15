package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.api.SaveDataFromAPI;
import org.example.service.UserService;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        UserService userService = new UserService();
        userService.initDefaultAdmin();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/WelcomeView.fxml")
        );
        Scene scene = new Scene(loader.load(), 1050, 700);
        stage.setScene(scene);
        stage.setTitle("Hệ thống bán hàng Hobbee");
        try {
            Image icon = new Image(getClass().getResourceAsStream("/asset/logo.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Không tìm thấy file logo, vui lòng kiểm tra lại đường dẫn!");
        }
        stage.show();
        Thread apiThread = new Thread(() -> {
            try {
                SaveDataFromAPI e = new SaveDataFromAPI();
                e.saveOrUpdateFromApi();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        apiThread.setDaemon(true);
        apiThread.start();
    }
}