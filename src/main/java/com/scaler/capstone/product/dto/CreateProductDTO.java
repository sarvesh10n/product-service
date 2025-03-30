package com.scaler.capstone.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @DecimalMin(value = "0.0", message = "Price must be at least 0")
    private double price;

    @Min(value = 0, message = "Quantity must be at least 0")
    private int stockQuantity;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    private double rating;

    @NotBlank(message = "Category cannot be blank")
    private String category;
}
