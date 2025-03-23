package com.scaler.capstone.product.models.order;

public enum OrderStatus {
    PENDING,    // Order created, payment not yet initiated
    PLACED,     // Order placed, payment successful
    CANCELLED,  // Order cancelled by user or admin
    FAILED      // Payment failed or order processing failed
}
