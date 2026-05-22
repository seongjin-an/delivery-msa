package com.ansj.delivery.restaurant.dto;

import com.ansj.delivery.restaurant.domain.RestaurantCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRestaurantRequest(
        @NotBlank(message = "가게명은 필수입니다.") String name,
        String phone,
        @NotBlank(message = "주소는 필수입니다.") String address,
        @NotNull(message = "카테고리는 필수입니다.") RestaurantCategory category,
        int minOrderAmount,
        int deliveryFee,
        int estimatedDeliveryMinutes
) {}
