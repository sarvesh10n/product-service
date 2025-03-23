package com.scaler.capstone.product.controllers;

import com.scaler.capstone.product.dto.AddCartItemDTO;
import com.scaler.capstone.product.dto.CartResponseDTO;
import com.scaler.capstone.product.exceptions.InsufficientStockException;
import com.scaler.capstone.product.exceptions.InvalidDataException;
import com.scaler.capstone.product.exceptions.NotFoundException;
import com.scaler.capstone.product.models.User;
import com.scaler.capstone.product.models.cart.Cart;
import com.scaler.capstone.product.repositories.UserRepository;
import com.scaler.capstone.product.services.CartService;
import com.scaler.capstone.product.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/carts")
public class CartController {
    private CartService cartService;
    private UserRepository userRepository;
    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/cart")
    public ResponseEntity<CartResponseDTO> addToCart(Authentication authentication, @RequestBody AddCartItemDTO addCartItemDTO) throws InsufficientStockException, NotFoundException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        Cart cart = cartService.addItemsToCart(user.getId(), addCartItemDTO);
        return new ResponseEntity<>(CartResponseDTO.fromCart(cart), HttpStatus.CREATED);
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponseDTO> getCart(Authentication authentication) throws NotFoundException, InvalidDataException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        Cart cart = cartService.getCartItems(user.getId());
        return new ResponseEntity<>(CartResponseDTO.fromCart(cart), HttpStatus.OK);
    }

    @PatchMapping("/cart")
    public ResponseEntity<CartResponseDTO> updateCart(Authentication authentication,
                                                      @RequestParam Long cartItemId, @RequestParam int quantity) throws InsufficientStockException, InvalidDataException, NotFoundException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        Cart cart = cartService.updateItemQuantityInCart(user.getId(), cartItemId, quantity);
        return new ResponseEntity<>(CartResponseDTO.fromCart(cart), HttpStatus.OK);
    }

    @PatchMapping("/cart/removeItem/{cartItemId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(Authentication authentication, @PathVariable Long cartItemId) throws InvalidDataException, NotFoundException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        Cart cart = cartService.removeItemsFromCart(user.getId(),cartItemId);
        return new ResponseEntity<>(CartResponseDTO.fromCart(cart), HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Void>  clearCart(Authentication authentication) throws NotFoundException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);
        cartService.clearCart(user.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
