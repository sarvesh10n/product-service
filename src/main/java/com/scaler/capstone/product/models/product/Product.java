package com.scaler.capstone.product.models.product;

import com.scaler.capstone.product.models.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Category category;
    private String title;
    private String description;
    private double price;
    private int stockQuantity;
    private double rating;
}