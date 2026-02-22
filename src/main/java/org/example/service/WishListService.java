package org.example.service;

import org.example.model.dto.WishListDTO;

import java.util.List;

public interface WishListService {
    void addWishList(int userId,int productId);
    List<WishListDTO> getAllWishList(int userId);
    boolean checkWishList(int userId, int productId);
    void deleteWishList(int userId, int productId);
}
