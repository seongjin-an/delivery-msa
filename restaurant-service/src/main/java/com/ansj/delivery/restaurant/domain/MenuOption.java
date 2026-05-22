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
@Table(name = "menu_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private String name;

    private boolean isRequired;
    private int maxSelectCount;

    @OneToMany(mappedBy = "menuOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuOptionItem> items = new ArrayList<>();

    @Builder
    public MenuOption(Menu menu, String name, boolean isRequired, int maxSelectCount) {
        this.menu = menu;
        this.name = name;
        this.isRequired = isRequired;
        this.maxSelectCount = maxSelectCount;
    }
}
