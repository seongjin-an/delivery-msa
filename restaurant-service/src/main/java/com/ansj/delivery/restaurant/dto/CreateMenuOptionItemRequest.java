package com.ansj.delivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMenuOptionItemRequest(
        @NotBlank(message = "옵션 항목명은 필수입니다.") String name,
        int extraPrice
) {}
