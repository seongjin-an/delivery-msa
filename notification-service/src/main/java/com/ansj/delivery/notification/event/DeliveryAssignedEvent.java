package com.ansj.delivery.notification.event;

import java.util.UUID;

public record DeliveryAssignedEvent(
        UUID deliveryId,
        UUID orderId,
        UUID riderId
) {}
