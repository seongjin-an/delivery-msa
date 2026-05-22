package com.ansj.delivery.restaurant.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantStatus status = RestaurantStatus.CLOSED;

    private int minOrderAmount;
    private int deliveryFee;
    private int estimatedDeliveryMinutes;

    @Column(nullable = false)
    private UUID ownerId;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @Builder
    public Restaurant(String name, String phone, String address, RestaurantCategory category,
                      int minOrderAmount, int deliveryFee, int estimatedDeliveryMinutes, UUID ownerId) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.category = category;
        this.minOrderAmount = minOrderAmount;
        this.deliveryFee = deliveryFee;
        this.estimatedDeliveryMinutes = estimatedDeliveryMinutes;
        this.ownerId = ownerId;
        this.status = RestaurantStatus.CLOSED;
    }

    public void update(String name, String phone, String address, RestaurantCategory category,
                       int minOrderAmount, int deliveryFee, int estimatedDeliveryMinutes) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.category = category;
        this.minOrderAmount = minOrderAmount;
        this.deliveryFee = deliveryFee;
        this.estimatedDeliveryMinutes = estimatedDeliveryMinutes;
    }

    public void open() {
        this.status = RestaurantStatus.OPEN;
    }

    public void close() {
        this.status = RestaurantStatus.CLOSED;
    }
}
