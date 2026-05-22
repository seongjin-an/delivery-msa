package com.ansj.delivery.delivery.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries",
       uniqueConstraints = @UniqueConstraint(name = "uk_delivery_order_id", columnNames = "order_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private UUID riderId;

    @Column(nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;

    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;

    @Builder
    public Delivery(UUID orderId, UUID riderId, String deliveryAddress) {
        this.orderId = orderId;
        this.riderId = riderId;
        this.deliveryAddress = deliveryAddress;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
    }

    public void pickup() {
        this.status = DeliveryStatus.PICKED_UP;
        this.pickedUpAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = DeliveryStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = DeliveryStatus.CANCELLED;
    }
}
