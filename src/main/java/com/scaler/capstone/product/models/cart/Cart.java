package com.scaler.capstone.product.models.cart;

import com.scaler.capstone.product.models.BaseModel;
import com.scaler.capstone.product.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Cart extends BaseModel {

    public Cart() {
        this.products = new ArrayList<>();
    }


    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> products;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double totalPrice;
}
