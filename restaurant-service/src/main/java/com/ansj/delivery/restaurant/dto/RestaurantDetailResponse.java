package com.ansj.delivery.restaurant.dto;

import com.ansj.delivery.restaurant.domain.Restaurant;
import com.ansj.delivery.restaurant.domain.RestaurantCategory;
import com.ansj.delivery.restaurant.domain.RestaurantStatus;

import java.util.List;
import java.util.UUID;

public record RestaurantDetailResponse(
        UUID id,
        String name,
        String phone,
        String address,
        RestaurantCategory category,
        RestaurantStatus status,
        int minOrderAmount,
        int deliveryFee,
        int estimatedDeliveryMinutes,
        UUID ownerId,
        List<MenuResponse> menus
) {
    public static RestaurantDetailResponse from(Restaurant restaurant) {
        List<MenuResponse> menus = restaurant.getMenus().stream()
                .map(MenuResponse::from)
                .toList();
        return new RestaurantDetailResponse(
                restaurant.getId(), restaurant.getName(), restaurant.getPhone(),
                restaurant.getAddress(), restaurant.getCategory(), restaurant.getStatus(),
                restaurant.getMinOrderAmount(), restaurant.getDeliveryFee(),
                restaurant.getEstimatedDeliveryMinutes(), restaurant.getOwnerId(), menus);
    }
}
