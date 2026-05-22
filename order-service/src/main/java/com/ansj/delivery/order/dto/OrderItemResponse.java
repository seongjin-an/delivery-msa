package com.ansj.delivery.order.dto;

import com.ansj.delivery.order.domain.OrderItem;

import java.util.List;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID menuId,
        String menuName,
        int unitPrice,
        int quantity,
        List<OrderItemOptionResponse> options
) {
    public static OrderItemResponse from(OrderItem item) {
        List<OrderItemOptionResponse> options = item.getOptions().stream()
                .map(OrderItemOptionResponse::from)
                .toList();
        return new OrderItemResponse(
                item.getId(), item.getMenuId(), item.getMenuName(),
                item.getUnitPrice(), item.getQuantity(), options);
    }
}
