package com.ansj.delivery.notification.service;

import com.ansj.delivery.common.exception.BusinessException;
import com.ansj.delivery.notification.domain.Notification;
import com.ansj.delivery.notification.dto.NotificationResponse;
import com.ansj.delivery.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getMyNotifications(UUID userId, boolean unreadOnly) {
        List<Notification> notifications;
        if (unreadOnly) {
            notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> BusinessException.notFound("알림을 찾을 수 없습니다."));

        if (!notification.getUserId().equals(userId)) {
            throw BusinessException.forbidden("해당 알림에 대한 권한이 없습니다.");
        }

        notification.markAsRead();
        return NotificationResponse.from(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(Notification::markAsRead);
    }
}
