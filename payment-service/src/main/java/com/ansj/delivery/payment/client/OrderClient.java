package com.ansj.delivery.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrder(@PathVariable UUID orderId);

    record OrderResponse(UUID id, UUID customerId, int totalAmount, String status) {}
}
