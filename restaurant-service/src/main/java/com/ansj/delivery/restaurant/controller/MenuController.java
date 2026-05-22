package com.ansj.delivery.restaurant.controller;

import com.ansj.delivery.common.response.ApiResponse;
import com.ansj.delivery.restaurant.dto.CreateMenuRequest;
import com.ansj.delivery.restaurant.dto.MenuResponse;
import com.ansj.delivery.restaurant.dto.UpdateMenuRequest;
import com.ansj.delivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/owner/restaurants/{restaurantId}/menus")
@RequiredArgsConstructor
public class MenuController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateMenuRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("메뉴가 등록되었습니다.", restaurantService.createMenu(ownerId, restaurantId, request)));
    }

    @PutMapping("/{menuId}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuId,
            @Valid @RequestBody UpdateMenuRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(restaurantService.updateMenu(ownerId, restaurantId, menuId, request)));
    }

    @DeleteMapping("/{menuId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(
            @RequestHeader("X-User-Id") UUID ownerId,
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuId) {
        restaurantService.deleteMenu(ownerId, restaurantId, menuId);
        return ResponseEntity.ok(ApiResponse.ok("메뉴가 삭제되었습니다.", null));
    }
}
