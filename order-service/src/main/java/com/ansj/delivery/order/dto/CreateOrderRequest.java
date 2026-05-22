package com.ansj.delivery.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "가게 ID는 필수입니다.") UUID restaurantId,
        @NotBlank(message = "배달 주소는 필수입니다.") String deliveryAddress,
        BigDecimal deliveryLat,
        BigDecimal deliveryLng,
        String requestNote,
        @NotEmpty(message = "주문 항목은 필수입니다.") List<CreateOrderItemRequest> items
) {}
