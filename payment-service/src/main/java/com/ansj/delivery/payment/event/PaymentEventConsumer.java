package com.ansj.delivery.payment.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * order.created 토픽을 수신하는 스텁 컨슈머.
 * 현재는 로그만 남기며, 향후 자동 결제 개시 등에 활용 가능하다.
 */
@Slf4j
@Component
public class PaymentEventConsumer {

    @KafkaListener(topics = "order.created", groupId = "payment-service-group")
    public void handleOrderCreated(Map<String, Object> payload) {
        log.info("[PaymentService] Received order.created event: orderId={}", payload.get("orderId"));
        // TODO: 필요 시 자동 결제 개시 로직 추가
    }
}
