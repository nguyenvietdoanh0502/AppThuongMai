package org.example.service.impl;

import org.example.api.CallApi;
import org.example.dao.ProductDAO;
import org.example.model.Product;
import org.example.service.ProductService;

import java.io.IOException;
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

    @Override
    public List<Product> getRelatedProduct(Product product) {
        List<Product> productList = productDAO.getAllProducts();
        List<Product> res = new ArrayList<>();
        for(Product x: productList){
            if(x.getCategory().equals(product.getCategory()) && x.getProductId()!=product.getProductId()){
                res.add(x);
            }
        }
        return res;
    }
}
