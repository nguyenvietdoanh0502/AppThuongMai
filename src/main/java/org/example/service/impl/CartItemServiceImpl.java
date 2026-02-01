package org.example.service.impl;

import org.example.dao.CartItemDAO;
import org.example.model.CartItem;
import org.example.model.dto.CartItemDTO;
import org.example.service.CartItemService;

import java.util.List;

public class CartItemServiceImpl implements CartItemService {
    private CartItemDAO cartItemDAO = new CartItemDAO();
    @Override
    public int countCartItems() {
        return cartItemDAO.countCartItem();
    }

    @Override
    public List<CartItem> getAllCartItems() {
        return cartItemDAO.getAllItems();
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        cartItemDAO.addCartItem(cartItem);
    }

    @Override
    public void removeCartItem(CartItemDTO cartItem) {
        cartItemDAO.removeCartItem(cartItem);
    }

    @Override
    public List<CartItemDTO> getCartItemInfo() {
        return cartItemDAO.getCartItemInfo();
    }

    @Override
    public void increaseQuantity(int productId,int newQty) {
        cartItemDAO.increaseQuantity(productId,newQty);
    }

    @Override
    public void decreaseQuantity(int productId) {
        cartItemDAO.decreaseQuantity(productId);
    }

}
