package org.example.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dao.CategoryDAO;
import org.example.dao.ProductDAO;
import org.example.model.Category;
import org.example.model.Product;
import org.example.service.CategoryService;

import java.util.ArrayList;
import java.util.List;


public class CategoryServiceImpl implements CategoryService {
    private CategoryDAO categoryDAO = new CategoryDAO();
    @Override
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
}
