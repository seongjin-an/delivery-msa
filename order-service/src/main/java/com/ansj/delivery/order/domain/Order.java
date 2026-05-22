package com.ansj.delivery.order.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    private String restaurantName;

    @Column(nullable = false)
    private String deliveryAddress;

    private BigDecimal deliveryLat;
    private BigDecimal deliveryLng;
    private int deliveryFee;
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    private String requestNote;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Builder
    public Order(UUID customerId, UUID restaurantId, String restaurantName, String deliveryAddress,
                 BigDecimal deliveryLat, BigDecimal deliveryLng, int deliveryFee, int totalAmount,
                 String requestNote) {
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.deliveryAddress = deliveryAddress;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
        this.requestNote = requestNote;
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public void changeStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }
}
