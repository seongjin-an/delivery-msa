package com.ansj.delivery.notification.controller;

import com.ansj.delivery.common.response.ApiResponse;
import com.ansj.delivery.notification.dto.NotificationResponse;
import com.ansj.delivery.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getMyNotifications(userId, unreadOnly)));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID notificationId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.markAsRead(userId, notificationId)));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestHeader("X-User-Id") UUID userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("모든 알림을 읽음 처리했습니다.", null));
    }
}
