package com.ansj.delivery.order.dto;

import com.ansj.delivery.order.domain.OrderItemOption;

import java.util.UUID;

public record OrderItemOptionResponse(
        UUID id,
        UUID optionItemId,
        String optionItemName,
        int extraPrice
) {
    public static OrderItemOptionResponse from(OrderItemOption option) {
        return new OrderItemOptionResponse(
                option.getId(), option.getOptionItemId(),
                option.getOptionItemName(), option.getExtraPrice());
    }
}
