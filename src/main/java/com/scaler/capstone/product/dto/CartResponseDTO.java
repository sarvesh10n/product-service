package com.scaler.capstone.product.dto;

import com.scaler.capstone.product.models.cart.Cart;
import com.scaler.capstone.product.models.cart.CartItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private double totalPrice;

    public static CartResponseDTO fromCart(Cart cart) {
        CartResponseDTO cartResponseDTO = new CartResponseDTO();
        cartResponseDTO.setId(cart.getId());
        cartResponseDTO.setUserId(cart.getUser().getId());
        cartResponseDTO.setTotalPrice(cart.getTotalPrice());

        List<CartItemResponseDTO> cartItems = new ArrayList<>();
        for(CartItem item: cart.getProducts())
        {
            CartItemResponseDTO cartItemResponse = new CartItemResponseDTO();
            cartItemResponse.setId(item.getId());
            cartItemResponse.setQuantity(item.getQuantity());
            cartItemResponse.setProductId(item.getProduct().getId());
            cartItemResponse.setProductName(item.getProduct().getTitle());
            cartItemResponse.setPrice(item.getProduct().getPrice());
            cartItems.add(cartItemResponse);
        }
        cartResponseDTO.setItems(cartItems);
        return cartResponseDTO;
    }
}
