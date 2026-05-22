package com.ansj.delivery.delivery.dto;

import java.util.UUID;

public record AvailableDeliveryResponse(
        UUID orderId,
        String restaurantName,
        String deliveryAddress,
        int totalAmount
) {}
