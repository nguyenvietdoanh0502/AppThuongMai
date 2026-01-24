package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.dao.ProductDAO;
import org.example.model.Product;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colTitle;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colCategory;

    @FXML private TextField txtTitle, txtPrice, txtQuantity, txtCategory, txtImage;
    @FXML private TextArea txtDescription;

    private final ProductDAO productDAO = new ProductDAO();
    private ObservableList<Product> productList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cấu hình các cột hiển thị
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        loadData();

        // Lắng nghe khi người dùng chọn một dòng trên bảng
        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetail(newSelection);
            }
        });
    }

    private void loadData() {
        try {
            productList = FXCollections.observableArrayList(productDAO.getAllProducts());
            tableProducts.setItems(productList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu từ Database!");
        }
    }

    private void showDetail(Product p) {
        txtTitle.setText(p.getTitle());
        txtPrice.setText(String.valueOf(p.getPrice()));
        txtQuantity.setText(String.valueOf(p.getQuantity()));
        txtCategory.setText(p.getCategory());
        txtImage.setText(p.getImage());
        txtDescription.setText(p.getDescription());
    }

    @FXML
    private void handleAdd() {
        if (validateInput()) {
            try {
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
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm sản phẩm mới!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm sản phẩm vào Database.");
            }
        }
    }

    @FXML
    private void handleUpdate() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn sản phẩm cần sửa trong bảng!");
            return;
        }

        if (validateInput()) {
            try {
                selected.setTitle(txtTitle.getText());
                selected.setPrice(Double.parseDouble(txtPrice.getText()));
                selected.setQuantity(Integer.parseInt(txtQuantity.getText()));
                selected.setCategory(txtCategory.getText());
                selected.setImage(txtImage.getText());
                selected.setDescription(txtDescription.getText());

                productDAO.updateProduct(selected);
                loadData();
                tableProducts.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật sản phẩm thành công!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thất bại.");
            }
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Hiển thị xác nhận trước khi xóa
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa: " + selected.getTitle() + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                productDAO.deleteProduct(selected.getProductId());
                loadData();
                handleClear();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn sản phẩm cần xóa!");
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

    // Hàm kiểm tra tính hợp lệ của dữ liệu nhập vào
    private boolean validateInput() {
        String errorMsg = "";

        if (txtTitle.getText().isEmpty()) errorMsg += "Tên sản phẩm không được để trống!\n";
        if (txtCategory.getText().isEmpty()) errorMsg += "Danh mục không được để trống!\n";

        try {
            Double.parseDouble(txtPrice.getText());
        } catch (NumberFormatException e) {
            errorMsg += "Giá sản phẩm phải là một số hợp lệ!\n";
        }

        try {
            Integer.parseInt(txtQuantity.getText());
        } catch (NumberFormatException e) {
            errorMsg += "Số lượng phải là một số nguyên!\n";
        }

        if (errorMsg.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.WARNING, "Dữ liệu không hợp lệ", errorMsg);
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}