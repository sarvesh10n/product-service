package com.scaler.capstone.product.services;

import com.scaler.capstone.product.exceptions.InsufficientStockException;
import com.scaler.capstone.product.exceptions.NotFoundException;
import com.scaler.capstone.product.models.User;
import com.scaler.capstone.product.models.cart.Cart;
import com.scaler.capstone.product.models.cart.CartItem;
import com.scaler.capstone.product.models.order.*;
import com.scaler.capstone.product.models.product.Product;
import com.scaler.capstone.product.repositories.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private UserRepository userRepository;
    private CartService cartService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository ,
                        ProductRepository productRepository, CartRepository cartRepository,
                        UserRepository userRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }


    public Order placeOrder(Long userId) throws NotFoundException, InsufficientStockException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found for id:"+userId));

        Cart cart = user.getCart();
        if(cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new NotFoundException("Cart is Empty");
        }

        // Create the order
        Order order = new Order();
        order.setUser(user);

        // Validate stock
        // Deduct stock
        // Create order item
        for (CartItem item : cart.getProducts()) {
            Product product = item.getProduct();

            // Validate stock
            if (product.getStockQuantity() < item.getQuantity())
            {
                throw new InsufficientStockException("Insufficient stock for product: "+ product.getTitle());
            }


            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        // calculate total and create payment intent transaction id
        double totalAmount = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
        order.setTotalAmount(totalAmount);

        //Generating Random Transaction Id, It should be provided by Payment microservice
        String transactionId = RandomStringUtils.randomAlphanumeric(25);
        order.setTransactionId(transactionId);
        order.setPaymentMethod(PaymentMethod.ONLINE);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTrackingNumber(RandomStringUtils.randomAlphanumeric(20));
        order.setOrderStatus(OrderStatus.PENDING);

        // Save the order
        orderRepository.save(order);

        // Clear the cart
        cartService.clearCart(userId);

        // Return the order with payment details (client completes payment on the frontend)
        return order;
    }

    public Order confirmPayment(String transactionId) throws NotFoundException {
        Order order = orderRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new NotFoundException("Order not found for id:"+transactionId));

        //Payment success
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PLACED);
        return orderRepository.save(order);

        //If payment failed
        //Restore product stock quantity and user cart items
    }

    public Order getOrder(Long orderId) throws NotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for id:"+orderId));
    }

    public List<Order> getAllOrders(Long userId, OrderStatus status) throws NotFoundException {

        Optional<List<Order>> orders = (status != null)
                ? orderRepository.findByUserIdAndOrderStatus(userId, status) // Filter by status
                : orderRepository.findByUserId(userId); // Fetch all orders

        if(orders.isEmpty()) {
            throw new NotFoundException("Order not found for id:"+userId);
        }
        return orders.get();
    }

    public Order cancelOrder(Long orderId) throws NotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for id:"+orderId));

        // Check if the order is already cancelled
        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new IllegalStateException("Order is already cancelled");
        }

        // If the order was paid, initiate a refund
        if (PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            //Initiate & Perform Refund
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        //When order is cancelled before confirming payment
        else {
            order.setPaymentStatus(PaymentStatus.CANCELLED);
        }

        // Restore stock for each item in the order
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        });

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

}
