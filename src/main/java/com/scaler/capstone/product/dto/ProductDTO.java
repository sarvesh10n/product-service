package com.scaler.capstone.product.dto;

import com.scaler.capstone.product.models.product.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @DecimalMin(value = "0.0", message = "Price must be at least 0")
    private Double price;

    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer stockQuantity;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    private Double rating;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    public static ProductDTO fromProduct(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setTitle(product.getTitle());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        productDTO.setRating(product.getRating());
        productDTO.setCategory(product.getCategory().getName());
        return productDTO;
    }
}