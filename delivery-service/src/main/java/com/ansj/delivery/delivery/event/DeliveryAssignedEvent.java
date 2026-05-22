package com.ansj.delivery.delivery.event;

import java.util.UUID;

public record DeliveryAssignedEvent(
        UUID deliveryId,
        UUID orderId,
        UUID riderId,
        String riderName
) {}
