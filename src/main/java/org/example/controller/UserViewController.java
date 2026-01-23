package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.example.api.CallApi;
import org.example.model.Product;
import org.example.service.ProductService;
import org.example.service.impl.ProductServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserViewController implements Initializable {
    private Node homeViewNode;
    public ScrollPane contentArea;
    public VBox sidebarFilter;
    public ColumnConstraints colSidebar;
    public ColumnConstraints colContent;
    @FXML
    private TilePane productContainer;
    private ProductService productService = ProductServiceImpl.getInstance();
    private CallApi apiProduct = new CallApi();
    @FXML
    private TextField txtSearch;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Product> products = null;
        try {
            products = apiProduct.getAllProducts();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        loadProducts(products);
        homeViewNode = contentArea.getContent();
        List<Product> finalProducts = products;
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterProduct(finalProducts,newValue);
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
    private void filterProduct(List<Product> products,String keyword){
        if (keyword == null || keyword.isEmpty()) {
            loadProducts(products);
            return;
        }
        List<Product> filteredList = new ArrayList<>();
        String searchKey = keyword.toLowerCase();
        for (Product p : products) {
            if (p.getTitle().toLowerCase().contains(searchKey)||p.getCategory().toLowerCase().contains(searchKey)) {
                filteredList.add(p);
            }
        }
        loadProducts(filteredList);
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
