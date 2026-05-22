package com.ansj.delivery.delivery.controller;

import com.ansj.delivery.common.response.ApiResponse;
import com.ansj.delivery.delivery.dto.AvailableDeliveryResponse;
import com.ansj.delivery.delivery.dto.DeliveryResponse;
import com.ansj.delivery.delivery.dto.RiderLocationRequest;
import com.ansj.delivery.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<AvailableDeliveryResponse>>> getAvailableDeliveries() {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getAvailableDeliveries()));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<DeliveryResponse>>> getMyDeliveries(
            @RequestHeader("X-User-Id") UUID riderId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.getMyDeliveries(riderId)));
    }

    @PostMapping("/accept/{orderId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> acceptDelivery(
            @RequestHeader("X-User-Id") UUID riderId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok("배달을 수락했습니다.", deliveryService.acceptDelivery(riderId, orderId)));
    }

    @PatchMapping("/{deliveryId}/pickup")
    public ResponseEntity<ApiResponse<DeliveryResponse>> pickup(
            @RequestHeader("X-User-Id") UUID riderId,
            @PathVariable UUID deliveryId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.pickup(riderId, deliveryId)));
    }

    @PatchMapping("/{deliveryId}/complete")
    public ResponseEntity<ApiResponse<DeliveryResponse>> complete(
            @RequestHeader("X-User-Id") UUID riderId,
            @PathVariable UUID deliveryId) {
        return ResponseEntity.ok(ApiResponse.ok(deliveryService.complete(riderId, deliveryId)));
    }

    @PostMapping("/location")
    public ResponseEntity<ApiResponse<Void>> updateLocation(
            @RequestHeader("X-User-Id") UUID riderId,
            @Valid @RequestBody RiderLocationRequest request) {
        deliveryService.updateLocation(riderId, request);
        return ResponseEntity.ok(ApiResponse.ok("위치가 업데이트되었습니다.", null));
    }
}
