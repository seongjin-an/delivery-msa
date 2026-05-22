package com.ansj.delivery.order.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "order_item_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    private UUID optionItemId;

    @Column(nullable = false)
    private String optionItemName;

    private int extraPrice;

    @Builder
    public OrderItemOption(OrderItem orderItem, UUID optionItemId, String optionItemName, int extraPrice) {
        this.orderItem = orderItem;
        this.optionItemId = optionItemId;
        this.optionItemName = optionItemName;
        this.extraPrice = extraPrice;
    }
}
