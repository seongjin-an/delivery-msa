package com.ansj.delivery.notification.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isRead = false;

    @Builder
    public Notification(UUID userId, String type, String title, String content) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
