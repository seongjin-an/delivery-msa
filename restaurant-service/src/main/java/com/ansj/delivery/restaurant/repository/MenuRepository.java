package com.ansj.delivery.restaurant.repository;

import com.ansj.delivery.restaurant.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {

    List<Menu> findByRestaurantIdOrderBySortOrder(UUID restaurantId);
}
