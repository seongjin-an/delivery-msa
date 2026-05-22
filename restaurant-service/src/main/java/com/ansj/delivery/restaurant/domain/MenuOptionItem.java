package com.ansj.delivery.restaurant.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "menu_option_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOptionItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id", nullable = false)
    private MenuOption menuOption;

    @Column(nullable = false)
    private String name;

    private int extraPrice;

    @Builder
    public MenuOptionItem(MenuOption menuOption, String name, int extraPrice) {
        this.menuOption = menuOption;
        this.name = name;
        this.extraPrice = extraPrice;
    }
}
