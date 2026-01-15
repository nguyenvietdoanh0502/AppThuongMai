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

        CallApiProduct service = new CallApiProduct();
        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = service.getAllProducts();

            System.out.println("Đã tìm thấy " + products.size() + " sản phẩm.");

            // Duyệt qua từng cái và in ra

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Product> products = productDAO.getAllProducts();
        for (var x : products) {
            System.out.println(x);
        }


    }
}