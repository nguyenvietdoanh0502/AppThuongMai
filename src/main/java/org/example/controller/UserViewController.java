package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.example.api.CallApi;
import org.example.model.Category;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class UserViewController implements Initializable {
    @FXML
    public VBox categoryContainer;
    public TextField minPrice;
    public TextField maxPrice;
    private Node homeViewNode;
    public ScrollPane contentArea;
    public VBox sidebarFilter;
    public ColumnConstraints colSidebar;
    public ColumnConstraints colContent;
    private double currentMinPrice = 0.0;
    private double currentMaxPrice = Double.MAX_VALUE;
    @FXML
    private TilePane productContainer;
    private ProductService productService = ProductServiceImpl.getInstance();
    private CallApi apiProduct = new CallApi();
    @FXML
    private TextField txtSearch;
    private Set<String> selectedCategories = new HashSet<>();
    private String currentSearchKeyword = "";
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Product> products = productService.getAllProduct();

        loadProducts(products);
        renderCategories(products);
        homeViewNode = contentArea.getContent();
        List<Product> finalProducts = products;
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            this.currentSearchKeyword = newVal.toLowerCase().trim();
            applyAllFilters(finalProducts);
        });
        minPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePriceRange();
            applyAllFilters(finalProducts);
        });

        maxPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            updatePriceRange();
            applyAllFilters(finalProducts);
        });
    }
    private void loadProducts(List<Product> products){
        productContainer.getChildren().clear();
        try{
            for(Product x: products){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/ProductCard.fxml"));
                VBox productBox = fxmlLoader.load();
                ProductCardController cardController = fxmlLoader.getController();
                cardController.setData(x);
                productBox.setOnMouseClicked(mouseEvent -> {
                    showProductInfor(x);
                });
                productContainer.getChildren().add(productBox);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void applyAllFilters(List<Product> masterData) {
        List<Product> filteredList = new ArrayList<>();
        for (Product p : masterData) {
            boolean matchKeyword = false;
            if (currentSearchKeyword.isEmpty()) {
                matchKeyword = true;
            } else {
                if (p.getTitle().toLowerCase().contains(currentSearchKeyword)) {
                    matchKeyword = true;
                }
            }
            boolean matchCategory = false;
            if (selectedCategories.isEmpty()) {
                matchCategory = true;
            } else {
                if (selectedCategories.contains(p.getCategory().toLowerCase())) {
                    matchCategory = true;
                }
            }
            boolean matchPrice = false;
            if(p.getPrice()>=this.currentMinPrice && p.getPrice()<=this.currentMaxPrice){
                matchPrice = true;
            }
            if (matchKeyword && matchCategory && matchPrice) {
                filteredList.add(p);
            }
        }
        loadProducts(filteredList);
    }
    private void renderCategories(List<Product> products){
        List<Category> categories = null;
        try{
            categories = apiProduct.getAllCategories();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        categoryContainer.getChildren().clear();
        for(Category cat: categories){
            HBox row = new HBox();
            row.setSpacing(10);
            row.setAlignment(Pos.CENTER_LEFT);
            CheckBox checkBox = new CheckBox();
            String rawName = cat.getName();
            Label label = new Label(rawName.substring(0, 1).toUpperCase() + rawName.substring(1));
            label.setFont(new Font(14));
            label.setStyle("-fx-text-fill: #333;");
            row.getChildren().addAll(checkBox,label);
            categoryContainer.getChildren().add(row);
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectedCategories.add(cat.getName());
                } else {
                    selectedCategories.remove(cat.getName());
                }
                applyAllFilters(products);
            });

            label.setOnMouseClicked(e -> checkBox.setSelected(!checkBox.isSelected()));
        }
    }
    private void updatePriceRange(){
        try{
            String minText = minPrice.getText().trim();
            if(minText.isEmpty()){
                this.currentMinPrice=0;
            }
            else{
                this.currentMinPrice = Double.parseDouble(minText);
            }
            String maxText = maxPrice.getText().trim();
            if(maxText.isEmpty()){
                this.currentMaxPrice = Double.MAX_VALUE;
            }
            else{
                this.currentMaxPrice = Double.parseDouble(maxText);
            }
        }catch (NumberFormatException e){
            minPrice.clear();
            maxPrice.clear();

        }
    }
    private void showProductInfor(Product product){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ProductInfor.fxml"));
            Parent inforView = loader.load();
            ProductInforController inforController = loader.getController();
            inforController.setData(product);
            inforController.setOnBackAction(()->{
                restoreSidebar();
                if (homeViewNode != null) {
                    contentArea.setContent(homeViewNode);
                }
                txtSearch.clear();
            });
            contentArea.setContent(inforView);
            collapseSidebar();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }private void collapseSidebar() {
        sidebarFilter.setVisible(false);
        sidebarFilter.setManaged(false);
        colSidebar.setPercentWidth(0);
        colSidebar.setMinWidth(0);
        colSidebar.setPrefWidth(0);
        colSidebar.setMaxWidth(0);
        colContent.setPercentWidth(100);
    }

    public void restoreSidebar() {
        sidebarFilter.setVisible(true);
        sidebarFilter.setManaged(true);
        colSidebar.setPercentWidth(20);
        colContent.setPercentWidth(80);
    }
}
