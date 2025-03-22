package com.scaler.capstone.product.dto;

import com.scaler.capstone.product.models.product.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private double price;
    private int stockQuantity;
    private double rating;
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