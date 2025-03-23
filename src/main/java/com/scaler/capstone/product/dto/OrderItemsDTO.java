package com.scaler.capstone.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double priceAtPurchase;
}
