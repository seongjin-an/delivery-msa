package com.ansj.delivery.restaurant.dto;

import com.ansj.delivery.restaurant.domain.MenuOptionItem;

import java.util.UUID;

public record MenuOptionItemResponse(
        UUID id,
        String name,
        int extraPrice
) {
    public static MenuOptionItemResponse from(MenuOptionItem item) {
        return new MenuOptionItemResponse(item.getId(), item.getName(), item.getExtraPrice());
    }
}
