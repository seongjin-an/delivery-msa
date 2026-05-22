package com.ansj.delivery.order.dto;

import com.ansj.delivery.order.domain.Order;
import com.ansj.delivery.order.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        UUID restaurantId,
        String restaurantName,
        String deliveryAddress,
        BigDecimal deliveryLat,
        BigDecimal deliveryLng,
        int deliveryFee,
        int totalAmount,
        OrderStatus status,
        String requestNote,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList();
        return new OrderResponse(
                order.getId(), order.getCustomerId(), order.getRestaurantId(),
                order.getRestaurantName(), order.getDeliveryAddress(),
                order.getDeliveryLat(), order.getDeliveryLng(), order.getDeliveryFee(),
                order.getTotalAmount(), order.getStatus(), order.getRequestNote(),
                items, order.getCreatedAt());
    }
}
