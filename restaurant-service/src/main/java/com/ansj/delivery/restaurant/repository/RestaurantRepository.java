package com.ansj.delivery.restaurant.repository;

import com.ansj.delivery.restaurant.domain.Restaurant;
import com.ansj.delivery.restaurant.domain.RestaurantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    List<Restaurant> findByStatus(RestaurantStatus status);

    List<Restaurant> findByOwnerId(UUID ownerId);
}
