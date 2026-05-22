package com.ansj.delivery.restaurant.dto;

import com.ansj.delivery.restaurant.domain.Restaurant;
import com.ansj.delivery.restaurant.domain.RestaurantCategory;
import com.ansj.delivery.restaurant.domain.RestaurantStatus;

import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String phone,
        String address,
        RestaurantCategory category,
        RestaurantStatus status,
        int minOrderAmount,
        int deliveryFee,
        int estimatedDeliveryMinutes,
        UUID ownerId
) {
    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(), restaurant.getName(), restaurant.getPhone(),
                restaurant.getAddress(), restaurant.getCategory(), restaurant.getStatus(),
                restaurant.getMinOrderAmount(), restaurant.getDeliveryFee(),
                restaurant.getEstimatedDeliveryMinutes(), restaurant.getOwnerId());
    }
}
