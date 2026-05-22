package com.ansj.delivery.notification.event;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        String restaurantName,
        int totalAmount
) {}
