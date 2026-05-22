package com.ansj.delivery.delivery.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rider_locations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiderLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID riderId;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Builder
    public RiderLocation(UUID riderId, BigDecimal latitude, BigDecimal longitude) {
        this.riderId = riderId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = LocalDateTime.now();
    }
}
