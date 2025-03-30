package com.scaler.capstone.product.services;

import com.scaler.capstone.product.clients.PaymentServiceClient;
import com.scaler.capstone.product.dto.CartItemDTO;
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

    public List<Order> getAllOrders(Long userId, OrderStatus status) throws NotFoundException {

        Optional<List<Order>> orders = (status != null)
                ? orderRepository.findByUserIdAndOrderStatus(userId, status)
                : orderRepository.findByUserId(userId);

        if(orders.isEmpty()) {
            throw new NotFoundException("Order Id not found: "+userId);
        }
        return orders.get();
    }

    public Order placeOrder(Long userId) throws NotFoundException, InsufficientStockException, PaymentClientException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id not found : "+userId));

        Cart cart = user.getCart();
        if(cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new NotFoundException("Cart is Empty");
        }


        Order order = new Order();
        order.setUser(user);


        for (CartItem item : cart.getProducts()) {
            Product product = item.getProduct();


            if (product.getStockQuantity() < item.getQuantity())
            {
                throw new InsufficientStockException("Insufficient stock for product: "+ product.getTitle());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);


            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }


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


        orderRepository.save(order);


        cartService.clearCart(userId);


        return order;
    }

    public Order getOrder(Long orderId) throws NotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order Id not found: "+orderId));
    }


    public Order confirmPayment(Long userId, String paymentOrderId) throws NotFoundException, PaymentClientException {

        Order order = orderRepository.findByPaymentOrderId(paymentOrderId)
                .orElseThrow(() -> new NotFoundException("Order Id not found: "+paymentOrderId));

        if (!OrderStatus.PENDING.equals(order.getOrderStatus())) {
            throw new IllegalStateException("Order is not waiting for payment status");
        }

        if (!PaymentStatus.PENDING.equals(order.getPaymentStatus())) {
            throw new IllegalStateException("Order payment status already provided. Payment status: "+order.getPaymentStatus());
        }

        PaymentClientDTO paymentClientDTO = paymentServiceClient.getPaymentStatus(paymentOrderId);

        if( paymentClientDTO.getPaymentId() == null || paymentClientDTO.getPaymentId().isBlank())
        {
            throw new IllegalStateException("Payment is not attempted.");
        }

        if("paid".equals(paymentClientDTO.getStatus()))
        {

            order.setPaymentId(paymentClientDTO.getPaymentId());
            order.setPaymentLink(null);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setOrderStatus(OrderStatus.PLACED);
            return orderRepository.save(order);
        }
        else {
            for(OrderItem orderItem : order.getOrderItems())
            {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);

                CartItemDTO cartItemDTO = new CartItemDTO();
                cartItemDTO.setProductId(product.getId());
                cartItemDTO.setQuantity(orderItem.getQuantity());
                try{
                    cartService.addItemsToCart(userId,cartItemDTO);
                }
                catch (InsufficientStockException ex) {
                    System.out.println("Insufficient stock for product: "+ orderItem.getProduct().getTitle());
                }
            }
            order.setPaymentId(paymentClientDTO.getPaymentId());
            order.setPaymentLink(null);
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.FAILED);
            return orderRepository.save(order);
        }
    }





    public Order cancelOrder(Long orderId) throws NotFoundException, PaymentClientException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order Id not found: "+orderId));

        if (OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
            throw new IllegalStateException("Order is already cancelled");
        }

        if (PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            PaymentClientDTO paymentClientDto = paymentServiceClient.processRefund(order.getPaymentOrderId());
            order.setRefundId(paymentClientDto.getRefundId());
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.setPaymentLink(null);
        }

        else {
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            order.setPaymentLink(null);
        }

        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        });

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

}

