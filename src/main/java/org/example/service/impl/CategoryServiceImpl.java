package org.example.service.impl;

import org.example.dao.CategoryDAO;
import org.example.dao.ProductDAO;
import org.example.model.Category;
import org.example.model.Product;
import org.example.service.CategoryService;

import java.util.ArrayList;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {
    private static CategoryServiceImpl instance;
    private static CategoryDAO categoryDAO;
    private List<Category> categories;
    private CategoryServiceImpl(){
        this.categories = new ArrayList<>();
        categoryDAO = new CategoryDAO();
    }
    public static CategoryServiceImpl getInstance(){
        if(instance==null){
            instance = new CategoryServiceImpl();
        }
        return instance;
    }
    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
}
