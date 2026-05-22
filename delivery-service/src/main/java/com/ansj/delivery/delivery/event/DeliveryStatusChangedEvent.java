package com.ansj.delivery.delivery.event;

import com.ansj.delivery.delivery.domain.DeliveryStatus;

import java.util.UUID;

public record DeliveryStatusChangedEvent(
        UUID deliveryId,
        UUID orderId,
        UUID riderId,
        DeliveryStatus status
) {}
