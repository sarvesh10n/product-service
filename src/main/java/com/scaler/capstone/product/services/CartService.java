package com.scaler.capstone.product.services;

import com.scaler.capstone.product.dto.AddCartItemDTO;
import com.scaler.capstone.product.repositories.CartItemRepository;
import com.scaler.capstone.product.repositories.CartRepository;
import com.scaler.capstone.product.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public void addItemsToCart(Long userId, AddCartItemDTO addCartItemDTO) {

    }

    public void removeItemsFromCart(Long userId, AddCartItemDTO addCartItemDTO) {

    }

    public void clearCart(Long userId) {

    }

    public void getCartItems(Long userId) {

    }

}
