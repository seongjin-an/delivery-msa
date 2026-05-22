package com.ansj.delivery.restaurant.controller;

import com.ansj.delivery.common.response.ApiResponse;
import com.ansj.delivery.restaurant.dto.*;
import com.ansj.delivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Public endpoints
    @GetMapping("/api/restaurants")
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getRestaurants() {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.getRestaurants()));
    }

    @GetMapping("/api/restaurants/{id}")
    public ResponseEntity<ApiResponse<RestaurantDetailResponse>> getRestaurant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.getRestaurant(id)));
    }

    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenusByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.getMenusByRestaurant(restaurantId)));
    }

    // Owner endpoints
    @PostMapping("/api/owner/restaurants")
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            @RequestHeader("X-User-Id") UUID ownerId,
            @Valid @RequestBody CreateRestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("가게가 등록되었습니다.", restaurantService.createRestaurant(ownerId, request)));
    }

    @PutMapping("/api/owner/restaurants/{restaurantId}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurant(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID restaurantId,
            @Valid @RequestBody UpdateRestaurantRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.updateRestaurant(ownerId, restaurantId, request)));
    }

    @PatchMapping("/api/owner/restaurants/{restaurantId}/open")
    public ResponseEntity<ApiResponse<RestaurantResponse>> openRestaurant(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.openRestaurant(ownerId, restaurantId)));
    }

    @PatchMapping("/api/owner/restaurants/{restaurantId}/close")
    public ResponseEntity<ApiResponse<RestaurantResponse>> closeRestaurant(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.closeRestaurant(ownerId, restaurantId)));
    }
}
