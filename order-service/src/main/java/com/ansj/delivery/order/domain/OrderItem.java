package com.ansj.delivery.order.domain;

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
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private UUID menuId;

    @Column(nullable = false)
    private String menuName;

    private int unitPrice;
    private int quantity;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> options = new ArrayList<>();

    @Builder
    public OrderItem(Order order, UUID menuId, String menuName, int unitPrice, int quantity) {
        this.order = order;
        this.menuId = menuId;
        this.menuName = menuName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
}
