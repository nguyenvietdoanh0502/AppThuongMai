package org.example.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CartItemDTO {
    private int cartItemId;
    private int userId;
    private int productId;
    private int quantity;
    private String title;
    private double price;
    private String image;
}
