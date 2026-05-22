package com.ansj.delivery.delivery.repository;

import com.ansj.delivery.delivery.domain.RiderLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RiderLocationRepository extends JpaRepository<RiderLocation, UUID> {

    Optional<RiderLocation> findTopByRiderIdOrderByRecordedAtDesc(UUID riderId);
}
