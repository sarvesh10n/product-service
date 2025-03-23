package com.scaler.capstone.product.controllers;

import com.scaler.capstone.product.dto.OrderDTO;
import com.scaler.capstone.product.exceptions.InsufficientStockException;
import com.scaler.capstone.product.exceptions.NotFoundException;
import com.scaler.capstone.product.models.User;
import com.scaler.capstone.product.models.order.Order;
import com.scaler.capstone.product.models.order.OrderStatus;
import com.scaler.capstone.product.repositories.UserRepository;
import com.scaler.capstone.product.services.OrderService;
import com.scaler.capstone.product.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;
    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    // Place order (initiate payment)
    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(Authentication authentication) throws InsufficientStockException, NotFoundException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);

        Order order = orderService.placeOrder(user.getId());
        return new ResponseEntity<>(OrderDTO.fromOrder(order), HttpStatus.CREATED);
    }

    // Confirm payment (called by frontend after payment completion)
    @PostMapping("/confirm-payment/{transactionId}")
    public ResponseEntity<OrderDTO> confirmPayment(@PathVariable String transactionId) throws NotFoundException {
        Order order = orderService.confirmPayment(transactionId);
        return new ResponseEntity<>(OrderDTO.fromOrder(order), HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) throws NotFoundException {
        Order order = orderService.getOrder(orderId);
        return new ResponseEntity<>(OrderDTO.fromOrder(order), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders(Authentication authentication,
                                                       @RequestParam(required = false) OrderStatus status) throws NotFoundException {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        User user = UserUtils.createUserIfNotExist(jwt, userRepository);
        List<Order> orders = orderService.getAllOrders(user.getId(), status);

        List<OrderDTO> orderDtoList =new ArrayList<>();
        for (Order order : orders) {
            orderDtoList.add(OrderDTO.fromOrder(order));
        }
        return new ResponseEntity<>(orderDtoList, HttpStatus.OK);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) throws NotFoundException {
        Order order = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(OrderDTO.fromOrder(order), HttpStatus.OK);
    }
}