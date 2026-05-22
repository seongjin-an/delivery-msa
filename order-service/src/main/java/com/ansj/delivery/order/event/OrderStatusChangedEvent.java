package com.ansj.delivery.order.event;

import com.ansj.delivery.order.domain.OrderStatus;

import java.util.UUID;

public record OrderStatusChangedEvent(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        OrderStatus status,
        String message
) {}
