package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private int cartItemId;
    private int userId;
    private int productId;
    private int quantity;

    public CartItem(int userId, int productId, int i) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = i;
    }
}
