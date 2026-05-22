package com.ansj.delivery.delivery.service;

import com.ansj.delivery.delivery.client.OrderClient;
import com.ansj.delivery.common.exception.BusinessException;
import com.ansj.delivery.delivery.domain.Delivery;
import com.ansj.delivery.delivery.domain.DeliveryStatus;
import com.ansj.delivery.delivery.domain.RiderLocation;
import com.ansj.delivery.delivery.dto.AvailableDeliveryResponse;
import com.ansj.delivery.delivery.dto.DeliveryResponse;
import com.ansj.delivery.delivery.dto.RiderLocationRequest;
import com.ansj.delivery.delivery.event.DeliveryAssignedEvent;
import com.ansj.delivery.delivery.event.DeliveryStatusChangedEvent;
import com.ansj.delivery.delivery.repository.DeliveryRepository;
import com.ansj.delivery.delivery.repository.RiderLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RiderLocationRepository riderLocationRepository;
    private final OrderClient orderClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public List<AvailableDeliveryResponse> getAvailableDeliveries() {
        return orderClient.getAvailableForDelivery().stream()
                .filter(order -> deliveryRepository.findByOrderId(order.id()).isEmpty())
                .map(order -> new AvailableDeliveryResponse(
                        order.id(), order.restaurantName(), order.deliveryAddress(), order.totalAmount()))
                .toList();
    }

    public List<DeliveryResponse> getMyDeliveries(UUID riderId) {
        return deliveryRepository.findByRiderIdOrderByAssignedAtDesc(riderId).stream()
                .map(DeliveryResponse::from)
                .toList();
    }

    @Transactional
    public DeliveryResponse acceptDelivery(UUID riderId, UUID orderId) {
        // Pessimistic lock check for race condition
        deliveryRepository.findByOrderIdWithLock(orderId).ifPresent(d -> {
            throw BusinessException.conflict("이미 배정된 주문입니다.");
        });

        // Get order details
        List<OrderClient.AvailableOrderResponse> available = orderClient.getAvailableForDelivery();
        OrderClient.AvailableOrderResponse order = available.stream()
                .filter(o -> o.id().equals(orderId))
                .findFirst()
                .orElseThrow(() -> BusinessException.notFound("배달 가능한 주문을 찾을 수 없습니다."));

        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .riderId(riderId)
                .deliveryAddress(order.deliveryAddress())
                .build();
        deliveryRepository.save(delivery);

        DeliveryAssignedEvent event = new DeliveryAssignedEvent(
                delivery.getId(), orderId, riderId, riderId.toString());
        kafkaTemplate.send("delivery.assigned", orderId.toString(), event);
        log.info("Delivery assigned: deliveryId={}, orderId={}, riderId={}", delivery.getId(), orderId, riderId);

        return DeliveryResponse.from(delivery);
    }

    @Transactional
    public DeliveryResponse pickup(UUID riderId, UUID deliveryId) {
        Delivery delivery = getOwnedDelivery(riderId, deliveryId);
        if (delivery.getStatus() != DeliveryStatus.ASSIGNED) {
            throw BusinessException.badRequest("배정 상태의 배달만 픽업 가능합니다.");
        }
        delivery.pickup();

        DeliveryStatusChangedEvent event = new DeliveryStatusChangedEvent(
                delivery.getId(), delivery.getOrderId(), riderId, DeliveryStatus.PICKED_UP);
        kafkaTemplate.send("delivery.picked_up", delivery.getOrderId().toString(), event);

        return DeliveryResponse.from(delivery);
    }

    @Transactional
    public DeliveryResponse complete(UUID riderId, UUID deliveryId) {
        Delivery delivery = getOwnedDelivery(riderId, deliveryId);
        if (delivery.getStatus() != DeliveryStatus.PICKED_UP) {
            throw BusinessException.badRequest("픽업 상태의 배달만 완료 처리 가능합니다.");
        }
        delivery.complete();

        DeliveryStatusChangedEvent event = new DeliveryStatusChangedEvent(
                delivery.getId(), delivery.getOrderId(), riderId, DeliveryStatus.DELIVERED);
        kafkaTemplate.send("delivery.completed", delivery.getOrderId().toString(), event);
        log.info("Delivery completed: deliveryId={}, orderId={}", deliveryId, delivery.getOrderId());

        return DeliveryResponse.from(delivery);
    }

    @Transactional
    public void updateLocation(UUID riderId, RiderLocationRequest request) {
        RiderLocation location = RiderLocation.builder()
                .riderId(riderId)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
        riderLocationRepository.save(location);
    }

    private Delivery getOwnedDelivery(UUID riderId, UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> BusinessException.notFound("배달을 찾을 수 없습니다."));
        if (!delivery.getRiderId().equals(riderId)) {
            throw BusinessException.forbidden("해당 배달에 대한 권한이 없습니다.");
        }
        return delivery;
    }
}
