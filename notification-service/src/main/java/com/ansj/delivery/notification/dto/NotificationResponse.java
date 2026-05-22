package com.ansj.delivery.notification.dto;

import com.ansj.delivery.notification.domain.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String type,
        String title,
        String content,
        boolean isRead,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(), notification.getUserId(), notification.getType(),
                notification.getTitle(), notification.getContent(), notification.isRead(),
                notification.getCreatedAt());
    }
}
