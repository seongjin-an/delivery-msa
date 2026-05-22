package com.ansj.delivery.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/api/restaurants/{restaurantId}")
    RestaurantResponse getRestaurant(@PathVariable UUID restaurantId);

    @GetMapping("/api/restaurants/{restaurantId}/menus/{menuId}")
    MenuResponse getMenu(@PathVariable UUID restaurantId, @PathVariable UUID menuId);

    @GetMapping("/api/restaurants/{restaurantId}/menus")
    List<MenuResponse> getMenusByRestaurant(@PathVariable UUID restaurantId);

    record RestaurantResponse(UUID id, String name, String address, int deliveryFee,
                              int minOrderAmount, String status) {}

    record MenuResponse(UUID id, String name, int price, boolean available) {}
}
