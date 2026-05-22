package com.ansj.delivery.restaurant.dto;

import com.ansj.delivery.restaurant.domain.Menu;

import java.util.List;
import java.util.UUID;

public record MenuResponse(
        UUID id,
        String name,
        String description,
        int price,
        String imageUrl,
        String categoryName,
        int sortOrder,
        boolean available,
        List<MenuOptionResponse> options
) {
    public static MenuResponse from(Menu menu) {
        List<MenuOptionResponse> options = menu.getOptions().stream()
                .map(MenuOptionResponse::from)
                .toList();
        return new MenuResponse(
                menu.getId(), menu.getName(), menu.getDescription(), menu.getPrice(),
                menu.getImageUrl(), menu.getCategoryName(), menu.getSortOrder(),
                menu.isAvailable(), options);
    }
}
