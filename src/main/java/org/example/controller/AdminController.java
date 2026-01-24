package org.example.controller;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.dao.ProductDAO;
import org.example.model.Product;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable{
    @FXML
    private TableView<Product> tableProducts;
    @FXML
    private TableColumn<Product, Integer> colId;
    @FXML
    private TableColumn<Product, String> colTitle;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Integer> colQuantity;
    @FXML
    private TableColumn<Product, String> colCategory;

    @FXML
    private TextField txtTitle, txtPrice, txtQuantity, txtCategory, txtImage;
    @FXML
    private TextArea txtDescription;

    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //mapping cột với thuộc tính trong model product
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        loadData();
        // hành đônng click vào dòng trong bảng
        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetail(newSelection);
            }
        });
    }

    private void loadData() {
        // Lấy dữ liệu từ DAO và đẩy vào TableView
        productList = FXCollections.observableArrayList(productDAO.getAllProducts());
        tableProducts.setItems(productList);
    }

    private void showDetail(Product p) {
        txtTitle.setText(p.getTitle());
        txtPrice.setText(String.valueOf(p.getPrice()));
        // ... set các trường còn lại
    }

    @FXML
    private void handleAdd() {
        Product p = new Product(
                0,
                txtTitle.getText(),
                Double.parseDouble(txtPrice.getText()),
                txtDescription.getText(),
                txtCategory.getText(),
                txtImage.getText(),
                0.0, 0,
                Integer.parseInt(txtQuantity.getText())
        );
        productDAO.addProduct(p);
        loadData();
        handleClear();
    }

    @FXML
    private void handleUpdate() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setTitle(txtTitle.getText());
            selected.setPrice(Double.parseDouble(txtPrice.getText()));
            selected.setQuantity(Integer.parseInt(txtQuantity.getText()));
            selected.setCategory(txtCategory.getText());
            selected.setImage(txtImage.getText());
            selected.setDescription(txtDescription.getText());

            productDAO.updateProduct(selected);
            loadData();
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productDAO.deleteProduct(selected.getProductId());
            loadData();
            handleClear();
        }
    }

    @FXML
    private void handleClear() {
        txtTitle.clear();
        txtPrice.clear();
        txtQuantity.clear();
        txtCategory.clear();
        txtImage.clear();
        txtDescription.clear();
        tableProducts.getSelectionModel().clearSelection();
    }
}
