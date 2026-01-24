package org.example.service;

import org.example.model.Product;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<Product> getAllProduct();
    List<Product> getRelatedProduct(Product product) throws IOException, InterruptedException;
}
