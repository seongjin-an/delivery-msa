package com.ansj.delivery.order.event;

import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        UUID restaurantId,
        String restaurantName,
        String deliveryAddress,
        int totalAmount,
        List<OrderItemInfo> items
) {
    public record OrderItemInfo(UUID menuId, String menuName, int unitPrice, int quantity) {}
}
