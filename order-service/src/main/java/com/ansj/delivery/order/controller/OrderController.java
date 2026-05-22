package com.ansj.delivery.order.controller;

import com.ansj.delivery.common.response.ApiResponse;
import com.ansj.delivery.order.domain.OrderStatus;
import com.ansj.delivery.order.dto.CancelOrderRequest;
import com.ansj.delivery.order.dto.CreateOrderRequest;
import com.ansj.delivery.order.dto.OrderResponse;
import com.ansj.delivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Customer endpoints
    @PostMapping("/api/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("X-User-Id") UUID customerId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("주문이 생성되었습니다.", orderService.createOrder(customerId, request)));
    }

    @GetMapping("/api/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @RequestHeader("X-User-Id") UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getMyOrders(customerId)));
    }

    @GetMapping("/api/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrder(customerId, orderId)));
    }

    @PatchMapping("/api/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable UUID orderId,
            @RequestBody(required = false) CancelOrderRequest request) {
        String reason = request != null ? request.reason() : null;
        return ResponseEntity.ok(ApiResponse.ok(orderService.cancelOrder(customerId, orderId, reason)));
    }

    // Internal endpoint for delivery-service
    @GetMapping("/api/orders/available-for-delivery")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAvailableForDelivery() {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAvailableForDelivery()));
    }

    // Owner endpoints
    @GetMapping("/api/owner/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRestaurantOrders(
            @RequestHeader("X-User-Id") UUID ownerId,
            @RequestParam UUID restaurantId,
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getRestaurantOrders(ownerId, restaurantId, status)));
    }

    @PatchMapping("/api/owner/orders/{orderId}/accept")
    public ResponseEntity<ApiResponse<OrderResponse>> acceptOrder(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.acceptOrder(ownerId, orderId)));
    }

    @PatchMapping("/api/owner/orders/{orderId}/ready")
    public ResponseEntity<ApiResponse<OrderResponse>> markReady(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.markReady(ownerId, orderId)));
    }
}
