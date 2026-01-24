package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.api.SaveDataFromAPI;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/AccountView.fxml")
        );
        Scene scene = new Scene(loader.load(),1050,700);
        stage.setScene(scene);
        stage.setTitle("Test");
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