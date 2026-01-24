package org.example.model;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    private int productId;
    private String title;
    private double price;
    private String description;
    private String category;
    private String image;
    private double ratingRate;
    private int ratingCount;
    private int quantity;
}
