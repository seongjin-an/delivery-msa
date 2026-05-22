package com.ansj.delivery.delivery.repository;

import com.ansj.delivery.delivery.domain.Delivery;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    List<Delivery> findByRiderIdOrderByAssignedAtDesc(UUID riderId);

    Optional<Delivery> findByOrderId(UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Delivery d WHERE d.orderId = :orderId")
    Optional<Delivery> findByOrderIdWithLock(@Param("orderId") UUID orderId);
}
