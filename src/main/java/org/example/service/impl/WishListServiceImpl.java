package org.example.service.impl;

import org.example.dao.WishListDAO;
import org.example.model.dto.WishListDTO;
import org.example.service.WishListService;

import java.util.List;

public class WishListServiceImpl implements WishListService {
    private WishListDAO wishListDAO = new WishListDAO();
    @Override
    public void addWishList(int userId, int productId) {
        wishListDAO.addWishList(userId,productId);
    }

    @Override
    public List<WishListDTO> getAllWishList(int userId) {
        return wishListDAO.getAllWishList(userId);
    }

    @Override
    public boolean checkWishList(int userId, int productId) {
        return wishListDAO.checkWishList(userId,productId);
    }

    @Override
    public void deleteWishList(int userId, int productId) {
        wishListDAO.deleteWishList(userId,productId);
    }
}
