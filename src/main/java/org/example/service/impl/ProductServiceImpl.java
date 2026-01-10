package org.example.service.impl;

import org.example.dao.ProductDAO;
import org.example.model.Product;
import org.example.service.ProductService;

import java.util.ArrayList;
import java.util.List;

public class ProductServiceImpl implements ProductService {

    private static ProductServiceImpl instance;
    private static ProductDAO productDAO;
    private List<Product> products;
    private ProductServiceImpl(){
        this.products = new ArrayList<>();
        productDAO = new ProductDAO();
    }
    public static ProductServiceImpl getInstance(){
        if(instance==null){
            instance = new ProductServiceImpl();
        }
        return instance;
    }
    @Override
    public List<Product> getAllProduct() {
        return productDAO.getAllProducts();
    }
}
