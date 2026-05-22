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
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int price;
    private String imageUrl;
    private String categoryName;
    private int sortOrder;
    private boolean available = true;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuOption> options = new ArrayList<>();

    @Builder
    public Menu(Restaurant restaurant, String name, String description, int price,
                String imageUrl, String categoryName, int sortOrder) {
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.sortOrder = sortOrder;
        this.available = true;
    }

    public void update(String name, String description, int price, String imageUrl,
                       String categoryName, int sortOrder, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.sortOrder = sortOrder;
        this.available = available;
    }
}
