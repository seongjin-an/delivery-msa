package com.ansj.delivery.restaurant.repository;

import com.ansj.delivery.restaurant.domain.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuOptionRepository extends JpaRepository<MenuOption, UUID> {

    List<MenuOption> findByMenuId(UUID menuId);
}
