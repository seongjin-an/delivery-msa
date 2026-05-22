package com.ansj.delivery.order.service;

import com.ansj.delivery.order.client.RestaurantClient;
import com.ansj.delivery.common.exception.BusinessException;
import com.ansj.delivery.order.domain.*;
import com.ansj.delivery.order.dto.*;
import com.ansj.delivery.order.event.OrderCreatedEvent;
import com.ansj.delivery.order.event.OrderStatusChangedEvent;
import com.ansj.delivery.order.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantClient restaurantClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public OrderResponse createOrder(UUID customerId, CreateOrderRequest request) {
        // Validate restaurant via Feign
        RestaurantClient.RestaurantResponse restaurant;
        try {
            restaurant = restaurantClient.getRestaurant(request.restaurantId());
        } catch (Exception e) {
            throw BusinessException.notFound("가게를 찾을 수 없습니다.");
        }

        if (!"OPEN".equals(restaurant.status())) {
            throw BusinessException.badRequest("현재 영업 중인 가게가 아닙니다.");
        }

        // Fetch menus and calculate total
        List<RestaurantClient.MenuResponse> menus;
        try {
            menus = restaurantClient.getMenusByRestaurant(request.restaurantId());
        } catch (Exception e) {
            throw BusinessException.badRequest("메뉴 정보를 가져올 수 없습니다.");
        }

        int totalAmount = restaurant.deliveryFee();
        for (CreateOrderItemRequest itemReq : request.items()) {
            RestaurantClient.MenuResponse menu = menus.stream()
                    .filter(m -> m.id().equals(itemReq.menuId()))
                    .findFirst()
                    .orElseThrow(() -> BusinessException.notFound("메뉴를 찾을 수 없습니다: " + itemReq.menuId()));
            if (!menu.available()) {
                throw BusinessException.badRequest("주문 불가능한 메뉴입니다: " + menu.name());
            }
            int itemTotal = menu.price() * itemReq.quantity();
            if (itemReq.options() != null) {
                itemTotal += itemReq.options().stream().mapToInt(o -> o.extraPrice() * itemReq.quantity()).sum();
            }
            totalAmount += itemTotal;
        }

        if (totalAmount - restaurant.deliveryFee() < restaurant.minOrderAmount()) {
            throw BusinessException.badRequest("최소 주문 금액을 충족하지 못했습니다.");
        }

        // Build Order
        Order order = Order.builder()
                .customerId(customerId)
                .restaurantId(request.restaurantId())
                .restaurantName(restaurant.name())
                .deliveryAddress(request.deliveryAddress())
                .deliveryLat(request.deliveryLat())
                .deliveryLng(request.deliveryLng())
                .deliveryFee(restaurant.deliveryFee())
                .totalAmount(totalAmount)
                .requestNote(request.requestNote())
                .build();
        orderRepository.save(order);

        // Build items
        for (CreateOrderItemRequest itemReq : request.items()) {
            RestaurantClient.MenuResponse menu = menus.stream()
                    .filter(m -> m.id().equals(itemReq.menuId()))
                    .findFirst().orElseThrow();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuId(itemReq.menuId())
                    .menuName(menu.name())
                    .unitPrice(menu.price())
                    .quantity(itemReq.quantity())
                    .build();

            if (itemReq.options() != null) {
                for (var optReq : itemReq.options()) {
                    OrderItemOption option = OrderItemOption.builder()
                            .orderItem(orderItem)
                            .optionItemId(optReq.optionItemId())
                            .optionItemName(optReq.optionItemName())
                            .extraPrice(optReq.extraPrice())
                            .build();
                    orderItem.getOptions().add(option);
                }
            }
            order.getItems().add(orderItem);
        }

        orderRepository.save(order);

        // Publish event
        List<OrderCreatedEvent.OrderItemInfo> itemInfos = order.getItems().stream()
                .map(i -> new OrderCreatedEvent.OrderItemInfo(i.getMenuId(), i.getMenuName(), i.getUnitPrice(), i.getQuantity()))
                .toList();

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(), order.getCustomerId(), order.getRestaurantId(),
                order.getRestaurantName(), order.getDeliveryAddress(), order.getTotalAmount(), itemInfos);

        kafkaTemplate.send("order.created", order.getId().toString(), event);
        log.info("Published order.created event for orderId: {}", order.getId());

        return OrderResponse.from(order);
    }

    public List<OrderResponse> getMyOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse getOrder(UUID customerId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (!order.getCustomerId().equals(customerId)) {
            throw BusinessException.forbidden("해당 주문에 대한 권한이 없습니다.");
        }
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(UUID customerId, UUID orderId, String reason) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (!order.getCustomerId().equals(customerId)) {
            throw BusinessException.forbidden("해당 주문에 대한 권한이 없습니다.");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAID) {
            throw BusinessException.badRequest("현재 상태에서는 취소할 수 없습니다: " + order.getStatus());
        }
        order.changeStatus(OrderStatus.CANCELLED);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getId(), order.getCustomerId(), order.getRestaurantId(),
                OrderStatus.CANCELLED, reason != null ? reason : "고객 요청으로 취소");
        kafkaTemplate.send("order.status.changed", order.getId().toString(), event);

        return OrderResponse.from(order);
    }

    public List<OrderResponse> getRestaurantOrders(UUID ownerId, UUID restaurantId, OrderStatus status) {
        if (status != null) {
            return orderRepository.findByRestaurantIdAndStatusOrderByCreatedAtDesc(restaurantId, status).stream()
                    .map(OrderResponse::from)
                    .toList();
        }
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse acceptOrder(UUID ownerId, UUID orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (order.getStatus() != OrderStatus.PAID) {
            throw BusinessException.badRequest("결제 완료 상태의 주문만 수락 가능합니다.");
        }
        order.changeStatus(OrderStatus.ACCEPTED);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getId(), order.getCustomerId(), order.getRestaurantId(),
                OrderStatus.ACCEPTED, "가게에서 주문을 수락했습니다.");
        kafkaTemplate.send("order.accepted", order.getId().toString(), event);

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse markReady(UUID ownerId, UUID orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.COOKING) {
            throw BusinessException.badRequest("수락 또는 조리 중 상태의 주문만 픽업 준비 가능합니다.");
        }
        order.changeStatus(OrderStatus.READY_FOR_PICKUP);

        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                order.getId(), order.getCustomerId(), order.getRestaurantId(),
                OrderStatus.READY_FOR_PICKUP, "음식 준비가 완료되었습니다.");
        kafkaTemplate.send("order.ready", order.getId().toString(), event);

        return OrderResponse.from(order);
    }

    @Transactional
    public void handlePaymentCompleted(UUID orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            order.changeStatus(OrderStatus.PAID);
            log.info("Order {} status changed to PAID", orderId);
        }
    }

    @Transactional
    public void handlePaymentFailed(UUID orderId) {
        Order order = orderRepository.findByIdWithLock(orderId)
                .orElseThrow(() -> BusinessException.notFound("주문을 찾을 수 없습니다."));
        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            order.changeStatus(OrderStatus.CANCELLED);
            log.info("Order {} status changed to CANCELLED due to payment failure", orderId);
        }
    }

    public List<OrderResponse> getAvailableForDelivery() {
        return orderRepository.findReadyForPickup().stream()
                .map(OrderResponse::from)
                .toList();
    }
}
