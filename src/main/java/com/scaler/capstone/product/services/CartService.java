package com.scaler.capstone.product.services;

import com.scaler.capstone.product.dto.CartItemDTO;
import com.scaler.capstone.product.exceptions.InsufficientStockException;
import com.scaler.capstone.product.exceptions.InvalidDataException;
import com.scaler.capstone.product.exceptions.NotFoundException;
import com.scaler.capstone.product.models.User;
import com.scaler.capstone.product.models.cart.Cart;
import com.scaler.capstone.product.models.cart.CartItem;
import com.scaler.capstone.product.models.product.Product;
import com.scaler.capstone.product.repositories.CartRepository;
import com.scaler.capstone.product.repositories.ProductRepository;
import com.scaler.capstone.product.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart addItemsToCart(Long userId, CartItemDTO cartItemDTO) throws NotFoundException,
            InsufficientStockException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found : "+userId));

        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found for id:"
                        + cartItemDTO.getProductId()));
        if(product.isDeleted())
        {throw new NotFoundException("Product not found for id:"+product.getId());}

        if (product.getStockQuantity() < cartItemDTO.getQuantity()) {
            throw new InsufficientStockException("Product stock is insufficient");
        }

        Cart cart = user.getCart();
        if(cart == null)
        {
            cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }

        Optional<CartItem> existingItem = cart.getProducts().stream().
                filter(item -> item.getProduct().getId().equals(cartItemDTO.getProductId())).findFirst();

        if(existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + cartItemDTO.getQuantity());
        }
        else {
            CartItem item = new CartItem();
            item.setQuantity(cartItemDTO.getQuantity());
            item.setProduct(product);
            item.setCart(cart);
            cart.getProducts().add(item);
        }

        cart.setTotalPrice(getCartTotal(cart));
        return cartRepository.save(cart);
    }

    public Cart getCartItems(Long userId) throws NotFoundException, InvalidDataException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found : "+userId));
        Cart cart = user.getCart();
        if(cart == null)
        {
            throw new InvalidDataException("Cart is empty for user: "+ userId);
        }
        return cart;
    }



    public void clearCart(Long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found : "+userId));

        Cart cart = user.getCart();
        if(cart!=null)
        {
            cart.getProducts().clear();
            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
        }
    }


    public Cart removeItemsFromCart(Long userId, Long cartItemId) throws NotFoundException, InvalidDataException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found : "+userId));

        Cart cart = user.getCart();
        if(cart == null || cart.getProducts().isEmpty())
        {
            throw new NotFoundException("Cart is empty for user: "+ userId);
        }

        CartItem itemToRemove = cart.getProducts().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found for id: "+cartItemId));


        cart.getProducts().remove(itemToRemove);

        cart.setTotalPrice(getCartTotal(cart));
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantityInCart(Long userId, Long cartItemId, int updateQuantity)
            throws NotFoundException, InvalidDataException, InsufficientStockException {
        if(updateQuantity<1)
        {
            throw new InvalidDataException("Invalid quantity");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found : "+userId));

        Cart cart = user.getCart();
        if(cart == null || cart.getProducts().isEmpty())
        {
            throw new NotFoundException("Cart is empty for user: "+ userId);
        }

        CartItem itemToUpdate = cart.getProducts().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item not found for item id: "+ cartItemId));

        Product product = itemToUpdate.getProduct();
        if(product.getStockQuantity() < updateQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: "+product.getId());
        }

        itemToUpdate.setQuantity(updateQuantity);
        cart.setTotalPrice(getCartTotal(cart));
        return cartRepository.save(cart);
    }


    private double getCartTotal(Cart cart) {
        List<CartItem> cartItems = cart.getProducts();
        double total = 0;
        for (CartItem cartItem : cartItems) {
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        return total;
    }

}
