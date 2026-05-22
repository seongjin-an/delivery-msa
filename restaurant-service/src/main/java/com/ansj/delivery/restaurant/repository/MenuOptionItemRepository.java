package com.ansj.delivery.restaurant.repository;

import com.ansj.delivery.restaurant.domain.MenuOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuOptionItemRepository extends JpaRepository<MenuOptionItem, UUID> {

    List<MenuOptionItem> findByMenuOptionId(UUID menuOptionId);
}
