package com.ansj.delivery.order.repository;

import com.ansj.delivery.order.domain.Order;
import com.ansj.delivery.order.domain.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdWithLock(@Param("id") UUID id);

    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<Order> findByRestaurantIdOrderByCreatedAtDesc(UUID restaurantId);

    List<Order> findByRestaurantIdAndStatusOrderByCreatedAtDesc(UUID restaurantId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = 'READY_FOR_PICKUP'")
    List<Order> findReadyForPickup();
}
