package com.ansj.delivery.order.event;

import com.ansj.delivery.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "payment.completed", groupId = "order-service-group")
    public void handlePaymentCompleted(Map<String, Object> payload) {
        try {
            UUID orderId = UUID.fromString((String) payload.get("orderId"));
            log.info("Payment completed for orderId: {}", orderId);
            orderService.handlePaymentCompleted(orderId);
        } catch (Exception e) {
            log.error("Failed to handle payment.completed event", e);
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "order-service-group")
    public void handlePaymentFailed(Map<String, Object> payload) {
        try {
            UUID orderId = UUID.fromString((String) payload.get("orderId"));
            log.info("Payment failed for orderId: {}", orderId);
            orderService.handlePaymentFailed(orderId);
        } catch (Exception e) {
            log.error("Failed to handle payment.failed event", e);
        }
    }
}
