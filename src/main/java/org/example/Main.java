package org.example;

import org.example.api.CallApiProduct;
import org.example.dao.ProductDAO;
import org.example.model.Product;

import java.util.Scanner;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/UserView.fxml")
        );
        Scene scene = new Scene(loader.load(),1050,600);
        stage.setScene(scene);
        stage.setTitle("Test");
        stage.show();
    }
}