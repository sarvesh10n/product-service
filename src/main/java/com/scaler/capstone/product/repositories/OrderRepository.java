package com.scaler.capstone.product.repositories;

import com.scaler.capstone.product.models.order.Order;
import com.scaler.capstone.product.models.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByPaymentOrderId(String paymentOrderId);

    Optional<List<Order>> findByUserId(Long user_id);

    Optional<List<Order>> findByUserIdAndOrderStatus(Long user_id, OrderStatus orderStatus);
}