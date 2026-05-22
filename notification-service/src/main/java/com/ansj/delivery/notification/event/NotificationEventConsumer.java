package com.ansj.delivery.notification.event;

import com.ansj.delivery.notification.domain.Notification;
import com.ansj.delivery.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "order.created", groupId = "notification-service-group")
    public void handleOrderCreated(Map<String, Object> payload) {
        try {
            UUID customerId = UUID.fromString((String) payload.get("customerId"));
            String restaurantName = (String) payload.get("restaurantName");
            Object totalAmountObj = payload.get("totalAmount");
            int totalAmount = totalAmountObj instanceof Number ? ((Number) totalAmountObj).intValue() : 0;

            Notification notification = Notification.builder()
                    .userId(customerId)
                    .type("ORDER_CREATED")
                    .title("주문이 접수되었습니다")
                    .content(String.format("%s에서 주문이 접수되었습니다. 결제를 완료해 주세요. (총 %,d원)", restaurantName, totalAmount))
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for order.created: userId={}", customerId);
        } catch (Exception e) {
            log.error("Failed to handle order.created notification", e);
        }
    }

    @KafkaListener(topics = "payment.completed", groupId = "notification-service-group")
    public void handlePaymentCompleted(Map<String, Object> payload) {
        try {
            UUID customerId = UUID.fromString((String) payload.get("customerId"));
            Object amountObj = payload.get("amount");
            int amount = amountObj instanceof Number ? ((Number) amountObj).intValue() : 0;

            Notification notification = Notification.builder()
                    .userId(customerId)
                    .type("PAYMENT_COMPLETED")
                    .title("결제가 완료되었습니다")
                    .content(String.format("%,d원 결제가 완료되었습니다. 가게에서 주문을 확인 중입니다.", amount))
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for payment.completed: userId={}", customerId);
        } catch (Exception e) {
            log.error("Failed to handle payment.completed notification", e);
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-service-group")
    public void handlePaymentFailed(Map<String, Object> payload) {
        try {
            UUID customerId = UUID.fromString((String) payload.get("customerId"));
            String reason = (String) payload.get("reason");

            Notification notification = Notification.builder()
                    .userId(customerId)
                    .type("PAYMENT_FAILED")
                    .title("결제에 실패했습니다")
                    .content(String.format("결제 처리 중 오류가 발생했습니다. 사유: %s", reason))
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for payment.failed: userId={}", customerId);
        } catch (Exception e) {
            log.error("Failed to handle payment.failed notification", e);
        }
    }

    @KafkaListener(topics = "order.accepted", groupId = "notification-service-group")
    public void handleOrderAccepted(Map<String, Object> payload) {
        try {
            UUID customerId = UUID.fromString((String) payload.get("customerId"));

            Notification notification = Notification.builder()
                    .userId(customerId)
                    .type("ORDER_ACCEPTED")
                    .title("주문이 수락되었습니다")
                    .content("가게에서 주문을 수락하고 음식을 준비 중입니다.")
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for order.accepted: userId={}", customerId);
        } catch (Exception e) {
            log.error("Failed to handle order.accepted notification", e);
        }
    }

    @KafkaListener(topics = "delivery.assigned", groupId = "notification-service-group")
    public void handleDeliveryAssigned(Map<String, Object> payload) {
        try {
            // Notify based on orderId - we use riderId as notification target here too
            UUID riderId = UUID.fromString((String) payload.get("riderId"));

            Notification notification = Notification.builder()
                    .userId(riderId)
                    .type("DELIVERY_ASSIGNED")
                    .title("배달이 배정되었습니다")
                    .content("새로운 배달이 배정되었습니다. 매장으로 이동해 주세요.")
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for delivery.assigned: riderId={}", riderId);
        } catch (Exception e) {
            log.error("Failed to handle delivery.assigned notification", e);
        }
    }

    @KafkaListener(topics = "delivery.picked_up", groupId = "notification-service-group")
    public void handleDeliveryPickedUp(Map<String, Object> payload) {
        try {
            UUID riderId = UUID.fromString((String) payload.get("riderId"));

            Notification notification = Notification.builder()
                    .userId(riderId)
                    .type("DELIVERY_PICKED_UP")
                    .title("음식을 픽업했습니다")
                    .content("고객에게 음식을 배달해 주세요.")
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for delivery.picked_up: riderId={}", riderId);
        } catch (Exception e) {
            log.error("Failed to handle delivery.picked_up notification", e);
        }
    }

    @KafkaListener(topics = "delivery.completed", groupId = "notification-service-group")
    public void handleDeliveryCompleted(Map<String, Object> payload) {
        try {
            UUID riderId = UUID.fromString((String) payload.get("riderId"));

            Notification notification = Notification.builder()
                    .userId(riderId)
                    .type("DELIVERY_COMPLETED")
                    .title("배달이 완료되었습니다")
                    .content("배달이 성공적으로 완료되었습니다. 수고하셨습니다!")
                    .build();
            notificationRepository.save(notification);
            log.info("Notification created for delivery.completed: riderId={}", riderId);
        } catch (Exception e) {
            log.error("Failed to handle delivery.completed notification", e);
        }
    }
}
