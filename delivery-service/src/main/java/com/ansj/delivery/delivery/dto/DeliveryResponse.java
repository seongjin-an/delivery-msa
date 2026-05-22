package com.ansj.delivery.delivery.dto;

import com.ansj.delivery.delivery.domain.Delivery;
import com.ansj.delivery.delivery.domain.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID orderId,
        UUID riderId,
        String deliveryAddress,
        DeliveryStatus status,
        LocalDateTime assignedAt,
        LocalDateTime pickedUpAt,
        LocalDateTime deliveredAt
) {
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(), delivery.getOrderId(), delivery.getRiderId(),
                delivery.getDeliveryAddress(), delivery.getStatus(),
                delivery.getAssignedAt(), delivery.getPickedUpAt(), delivery.getDeliveredAt());
    }
}
