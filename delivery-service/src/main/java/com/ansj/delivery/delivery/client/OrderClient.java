package com.ansj.delivery.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/orders/available-for-delivery")
    List<AvailableOrderResponse> getAvailableForDelivery();

    record AvailableOrderResponse(UUID id, UUID restaurantId, String restaurantName,
                                  String deliveryAddress, int totalAmount) {}
}
