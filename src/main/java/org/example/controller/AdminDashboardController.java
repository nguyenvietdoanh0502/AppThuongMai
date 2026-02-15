package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.dao.DashboardDAO;
import org.example.model.dto.CustomerDTO;
import org.example.model.dto.ProductRevenueDTO;
import org.example.model.dto.ProductStatsDTO;
import org.example.model.dto.RevenueDTO;
import org.example.controller.login_controller.NavigationManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    public Label lblCount;
    public Label lblSum;
    public Button btnNavRevenue;
    public Button btnNavProduct;
    public Button btnNavCustomer;
    public VBox viewRevenue;
    public VBox viewProducts;
    public PieChart productPieChart;
    public VBox viewCustomers;
    public TableView tblCustomers;
    public PieChart revenuePieChart;
    public TableColumn colUsername;
    public TableColumn colOrderCount;
    public TableColumn colTotalSpent;
    @FXML
    private BarChart<String, Number> revenueChart;
    private DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRevenueData();
        setActiveTab(btnNavRevenue, viewRevenue);
    }

    private void resetButtonStyle(Button btn) {
        btn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-width: 0; " +
                        "-fx-text-fill: #555555; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-background-insets: 0;"
        );
    }

    private void setActiveTab(Button activeBtn, VBox activeView) {
        resetButtonStyle(btnNavCustomer);
        resetButtonStyle(btnNavProduct);
        resetButtonStyle(btnNavRevenue);
        activeBtn.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border-width: 0; " +
                        "-fx-text-fill: #4caf50; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 20 10 20; " +
                        "-fx-background-insets: 0;"
        );
        viewRevenue.setVisible(false);
        viewProducts.setVisible(false);
        viewCustomers.setVisible(false);
        activeView.setVisible(true);
    }

    private void loadRevenueData() {

        List<RevenueDTO> revenueDTOList = dashboardDAO.getDailyRevenue();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu ($)");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
        for (RevenueDTO item : revenueDTOList) {
            String dateStr = sdf.format(item.getDate());
            series.getData().add(new XYChart.Data<>(dateStr, item.getRevenue()));
        }
        revenueChart.getData().clear();
        revenueChart.getData().add(series);
        lblCount.setText(String.valueOf(dashboardDAO.getCountOrders()));
        lblSum.setText(String.valueOf(dashboardDAO.getSumOrders()) + "$");
    }

    public void loadProductData() {
        List<ProductStatsDTO> list = dashboardDAO.getTopSellingProducts();
        productPieChart.getData().clear();
        for (var x : list) {
            PieChart.Data slice = new PieChart.Data(x.getProductName(), x.getTotalSold());
            productPieChart.getData().add(slice);
            Tooltip.install(slice.getNode(), new Tooltip(x.getProductName() + ": " + x.getTotalSold()));
        }
        setupPieChartStyle(productPieChart, "Top Số Lượng");

        List<ProductRevenueDTO> list2 = dashboardDAO.getTopRevenueProducts();
        revenuePieChart.getData().clear();
        for (var x : list2) {
            PieChart.Data slice = new PieChart.Data(x.getProductName(), x.getTotalRevenue());
            revenuePieChart.getData().add(slice);
            Tooltip.install(slice.getNode(), new Tooltip(x.getProductName() + ": " + x.getTotalRevenue() + "$"));

        }
        setupPieChartStyle(revenuePieChart, "Top Doanh Thu");
    }

    private void setupPieChartStyle(PieChart chart, String title) {
        chart.setLabelsVisible(false);
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
        chart.setLegendVisible(true);
        chart.setTitle(null);
    }

    public void loadCustomerData() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colOrderCount.setCellValueFactory(new PropertyValueFactory<>("totalOrder"));
        colTotalSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        DashboardDAO dao = new DashboardDAO();
        List<CustomerDTO> list = dao.getTopCustomers();
        ObservableList<CustomerDTO> data = FXCollections.observableArrayList(list);
        tblCustomers.setItems(data);
    }

    public void switchView(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton == btnNavRevenue) {
            setActiveTab(btnNavRevenue, viewRevenue);
            loadRevenueData();
        } else if (clickedButton == btnNavProduct) {
            setActiveTab(btnNavProduct, viewProducts);
            loadProductData();
        } else if (clickedButton == btnNavCustomer) {
            setActiveTab(btnNavCustomer, viewCustomers);
            loadCustomerData();
        }
    }

    @FXML
    private void handleBackToAdmin(javafx.event.ActionEvent event) {
        NavigationManager.switchScene(event, "AdminView.fxml");
    }
}
