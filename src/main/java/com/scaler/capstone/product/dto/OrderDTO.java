package com.scaler.capstone.product.dto;

import com.scaler.capstone.product.models.order.Order;
import com.scaler.capstone.product.models.order.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private String status;
    private Date orderDate;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
    private String trackingNumber;
    private double totalAmount;
    private List<OrderItemsDTO> items;

    public static OrderDTO fromOrder(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setStatus(order.getOrderStatus().toString());
        dto.setOrderDate(order.getCreated_at());
        dto.setPaymentMethod(order.getPaymentMethod().toString());
        dto.setPaymentStatus(order.getPaymentStatus().toString());
        dto.setTransactionId(order.getTransactionId());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setTotalAmount(order.getTotalAmount());

        ArrayList<OrderItemsDTO> items = getOrderItemsDtos(order);
        dto.setItems(items);
        return dto;
    }

    private static ArrayList<OrderItemsDTO> getOrderItemsDtos(Order order) {
        ArrayList<OrderItemsDTO> items = new ArrayList<>();
        for(OrderItem item : order.getOrderItems())
        {
            OrderItemsDTO itemDto = new OrderItemsDTO();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getTitle());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
            items.add(itemDto);
        }
        return items;
    }
}
