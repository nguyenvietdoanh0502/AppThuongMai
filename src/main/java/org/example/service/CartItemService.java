package org.example.service;

import org.example.model.CartItem;
import org.example.model.dto.CartItemDTO;

import java.util.List;

public interface CartItemService {
    int countCartItems();
    List<CartItem> getAllCartItems();
    void addCartItem(CartItem cartItem);
    void removeCartItem(int id);
    List<CartItemDTO> getCartItemInfo();
    void increaseQuantity(int productId,int newQty);
    void decreaseQuantity(int productId);
}
