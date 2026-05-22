package com.ansj.delivery.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateMenuRequest(
        @NotBlank(message = "메뉴명은 필수입니다.") String name,
        String description,
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.") int price,
        String imageUrl,
        String categoryName,
        int sortOrder,
        boolean available
) {}
