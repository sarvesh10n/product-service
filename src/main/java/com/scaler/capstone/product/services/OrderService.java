package com.scaler.capstone.product.services;

import com.scaler.capstone.product.clients.PaymentServiceClient;
import com.scaler.capstone.product.dto.AddCartItemDTO;
import com.scaler.capstone.product.dto.PaymentClientDTO;
import com.scaler.capstone.product.exceptions.InsufficientStockException;
import com.scaler.capstone.product.exceptions.NotFoundException;
import com.scaler.capstone.product.exceptions.PaymentClientException;
import com.scaler.capstone.product.models.User;
import com.scaler.capstone.product.models.cart.Cart;
import com.scaler.capstone.product.models.cart.CartItem;
import com.scaler.capstone.product.models.order.*;
import com.scaler.capstone.product.models.product.Product;
import com.scaler.capstone.product.repositories.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private CartService cartService;
    private PaymentServiceClient paymentServiceClient;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        UserRepository userRepository, CartService cartService,
                        @Qualifier("razorpayPaymentClient") PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.paymentServiceClient = paymentServiceClient;
    }


    public Order placeOrder(Long userId) throws NotFoundException, InsufficientStockException, PaymentClientException {
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

        String invoiceNumber = RandomStringUtils.randomAlphanumeric(15);

        PaymentClientDTO paymentClientDto = paymentServiceClient.createPaymentOrder(invoiceNumber,"INR", totalAmount);

        order.setInvoiceNumber(invoiceNumber);
        order.setPaymentOrderId(paymentClientDto.getPaymentOrderId());
        order.setPaymentLink(paymentClientDto.getPaymentLink());
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

    public Order confirmPayment(Long userId, String paymentOrderId) throws NotFoundException, PaymentClientException {

        Order order = orderRepository.findByPaymentOrderId(paymentOrderId)
                .orElseThrow(() -> new NotFoundException("Order not found for id:"+paymentOrderId));

        // Check if the order is already cancelled
        if (!OrderStatus.PENDING.equals(order.getOrderStatus())) {
            throw new IllegalStateException("Order is not waiting for payment status");
        }

        // Check if the order is already cancelled
        if (!PaymentStatus.PENDING.equals(order.getPaymentStatus())) {
            throw new IllegalStateException("Order payment status already provided. Payment status: "+order.getPaymentStatus());
        }

        PaymentClientDTO paymentClientDto = paymentServiceClient.getPaymentStatus(paymentOrderId);

        //PaymentId will get updated only when payment is tried whether pass or fail
        if( paymentClientDto.getPaymentId() == null || paymentClientDto.getPaymentId().isBlank())
        {
            throw new IllegalStateException("Payment is not attempted.");
        }

        if("paid".equals(paymentClientDto.getStatus()))
        {
            //Payment success
            order.setPaymentId(paymentClientDto.getPaymentId());
            order.setPaymentLink(null);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.PLACED);
            return orderRepository.save(order);
        }
        else {
            //payment failed
            //Restore product stock quantity and user cart items
            for(OrderItem orderItem : order.getOrderItems())
            {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);

                AddCartItemDTO addCartItemtDto = new AddCartItemDTO();
                addCartItemtDto.setProductId(product.getId());
                addCartItemtDto.setQuantity(orderItem.getQuantity());
                try{
                    cartService.addItemsToCart(userId,addCartItemtDto);
                }
                catch (InsufficientStockException ex) {
                    //This scenario will occur when after adding stock other customer purchased it before adding it cart
                    System.out.println("Insufficient stock for product: "+ orderItem.getProduct().getTitle());
                }
            }
            order.setPaymentId(paymentClientDto.getPaymentId());
            order.setPaymentLink(null);
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.FAILED);
            return orderRepository.save(order);
        }
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

    public Order cancelOrder(Long orderId) throws NotFoundException, PaymentClientException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for id:"+orderId));

        // Check if the order is already cancelled
        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new IllegalStateException("Order is already cancelled");
        }

        // If the order was paid, initiate a refund
        if (PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            //Initiate & Perform Refund
            PaymentClientDTO paymentClientDto = paymentServiceClient.processRefund(order.getPaymentOrderId());
            order.setRefundId(paymentClientDto.getRefundId());
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setPaymentLink(null);
        }
        //When order is cancelled before confirming payment
        else {
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            order.setPaymentLink(null);
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

