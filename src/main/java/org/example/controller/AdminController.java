package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.example.dao.ProductDAO;
import org.example.dao.UserDAO;
import org.example.model.Product;
import org.example.model.User;
import org.example.model.Status;
import org.example.controller.login_controller.NavigationManager;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, Integer> colId, colQuantity;
    @FXML private TableColumn<Product, String> colTitle, colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TextField txtTitle, txtPrice, txtQuantity, txtCategory, txtImage, txtSearchProduct;
    @FXML private ImageView imgPreview;

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername, colEmail, colRole;
    @FXML private TableColumn<User, Status> colStatus;
    @FXML private TextField txtUsername, txtEmail, txtPassword, txtSearchUser;
    @FXML private ComboBox<String> cbRole;

    private final ProductDAO productDAO = new ProductDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Product> masterProductList = FXCollections.observableArrayList();
    private final ObservableList<User> masterUserList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Kích hoạt hỗ trợ các định dạng ảnh đặc biệt (WebP) từ thư viện TwelveMonkeys
        ImageIO.scanForPlugins();

        setupProductTable();
        setupUserTable();
        setupSearchLogic();
        loadData();
        loadUserData();

        // 2. Lắng nghe thay đổi ở ô nhập link ảnh để cập nhật hình xem trước ngay lập tức
        txtImage.textProperty().addListener((obs, oldV, newV) -> updateImagePreview(newV));
    }

    // --- XỬ LÝ ẢNH (QUAN TRỌNG) ---
    private void updateImagePreview(String url) {
        if (url == null || url.trim().isEmpty()) {
            imgPreview.setImage(null);
            return;
        }

        // Chạy trong Thread riêng để không làm lag giao diện khi tải ảnh từ Web
        new Thread(() -> {
            try {
                // backgroundLoading = true cho phép JavaFX tải ảnh ngầm
                Image image = new Image(url, true);

                image.errorProperty().addListener((obs, oldVal, isError) -> {
                    if (isError) {
                        System.err.println("Lỗi nạp ảnh từ: " + url);
                        Platform.runLater(() -> imgPreview.setImage(null));
                    }
                });

                image.progressProperty().addListener((obs, oldVal, progress) -> {
                    if (progress.doubleValue() == 1.0 && !image.isError()) {
                        Platform.runLater(() -> imgPreview.setImage(image));
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> imgPreview.setImage(null));
            }
        }).start();
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp"));

        File selectedFile = fileChooser.showOpenDialog(txtImage.getScene().getWindow());
        if (selectedFile != null) {
            // Chuyển đường dẫn file máy tính thành định dạng URL (file:/...)
            txtImage.setText(selectedFile.toURI().toString());
        }
    }

    // --- QUẢN LÝ SẢN PHẨM ---
    private void setupProductTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtTitle.setText(newV.getTitle());
                txtPrice.setText(String.valueOf(newV.getPrice()));
                txtQuantity.setText(String.valueOf(newV.getQuantity()));
                txtCategory.setText(newV.getCategory());
                txtImage.setText(newV.getImage()); // Tự động kích hoạt updateImagePreview qua listener
            }
        });
    }

    @FXML private void handleAdd() { saveProduct(true); }
    @FXML private void handleUpdate() { saveProduct(false); }

    private void saveProduct(boolean isNew) {
        try {
            Product p = isNew ? new Product() : tableProducts.getSelectionModel().getSelectedItem();
            if (p == null) return;
            p.setTitle(txtTitle.getText());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setQuantity(Integer.parseInt(txtQuantity.getText()));
            p.setCategory(txtCategory.getText());
            p.setImage(txtImage.getText());

            if (isNew) productDAO.addProduct(p); else productDAO.updateProduct(p);
            loadData();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu sản phẩm!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Dữ liệu không hợp lệ!");
        }
    }

    @FXML private void handleDelete() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected != null) { productDAO.deleteProduct(selected.getProductId()); loadData(); }
    }

    // --- QUẢN LÝ NGƯỜI DÙNG ---
    private void setupUserTable() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cbRole.setItems(FXCollections.observableArrayList("ADMIN", "USER"));

        tableUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                txtUsername.setText(newV.getUsername());
                txtEmail.setText(newV.getEmail());
                txtPassword.setText(newV.getPassword());
                if (newV.getRole() != null) cbRole.setValue(newV.getRole().name());
            }
        });
    }

    @FXML private void handleLockUser() { updateStatus(Status.LOCKED); }
    @FXML private void handleUnlockUser() { updateStatus(Status.ACTIVE); }

    private void updateStatus(Status s) {
        User u = tableUsers.getSelectionModel().getSelectedItem();
        if (u != null) { userDAO.updateUserStatus(u.getUserId(), s); loadUserData(); }
    }

    @FXML private void handleDeleteUser() {
        User u = tableUsers.getSelectionModel().getSelectedItem();
        if (u != null) { userDAO.deleteUser(u.getUserId()); loadUserData(); }
    }

    // --- SEARCH & LOAD DATA ---
    private void setupSearchLogic() {
        txtSearchProduct.textProperty().addListener((obs, old, val) -> {
            tableProducts.setItems(masterProductList.filtered(p -> val == null || val.isEmpty() ||
                    p.getTitle().toLowerCase().contains(val.toLowerCase())));
        });
        txtSearchUser.textProperty().addListener((obs, old, val) -> {
            tableUsers.setItems(masterUserList.filtered(u -> val == null || val.isEmpty() ||
                    u.getUsername().toLowerCase().contains(val.toLowerCase())));
        });
    }

    private void loadData() { masterProductList.setAll(productDAO.getAllProducts()); tableProducts.setItems(masterProductList); }
    private void loadUserData() { masterUserList.setAll(userDAO.getAllUsersOnly()); tableUsers.setItems(masterUserList); }

    // --- ĐIỀU HƯỚNG ---
    @FXML private void handleOpenStatistics(ActionEvent e) { NavigationManager.switchScene(e, "DashboardView.fxml"); }
    @FXML private void handleLogout(ActionEvent e) { NavigationManager.switchScene(e, "LoginView.fxml"); }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }
}