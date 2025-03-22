package com.scaler.capstone.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductDTO {
    private String title;
    private String description;
    private double price;
    private int stockQuantity;
    private double rating;
    private String category;
}
