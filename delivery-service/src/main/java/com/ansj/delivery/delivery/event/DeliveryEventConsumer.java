package com.ansj.delivery.delivery.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * order.ready 토픽을 수신하여 배달 준비 완료된 주문을 추적한다.
 * 실제 배달 배정은 라이더가 acceptDelivery API를 호출할 때 이루어진다.
 */
@Slf4j
@Component
public class DeliveryEventConsumer {

    @KafkaListener(topics = "order.ready", groupId = "delivery-service-group")
    public void handleOrderReady(Map<String, Object> payload) {
        log.info("[DeliveryService] Order ready for pickup: orderId={}, restaurantId={}",
                payload.get("orderId"), payload.get("restaurantId"));
        // 주문이 픽업 준비 완료 상태가 됨 - 라이더 앱에서 조회 가능하도록 로그 기록
        // 실제 배달 엔티티는 라이더가 수락할 때 생성됨
    }
}
