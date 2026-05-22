package com.ansj.delivery.notification.event;

import java.util.UUID;

public record DeliveryStatusChangedEvent(
        UUID deliveryId,
        UUID orderId,
        UUID riderId,
        String status
) {}
