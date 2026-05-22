package com.ansj.delivery.payment.service;

import com.ansj.delivery.payment.client.OrderClient;
import com.ansj.delivery.common.exception.BusinessException;
import com.ansj.delivery.payment.domain.Payment;
import com.ansj.delivery.payment.dto.PaymentRequest;
import com.ansj.delivery.payment.dto.PaymentResponse;
import com.ansj.delivery.payment.event.PaymentCompletedEvent;
import com.ansj.delivery.payment.event.PaymentFailedEvent;
import com.ansj.delivery.payment.pg.PgClient;
import com.ansj.delivery.payment.pg.PgResult;
import com.ansj.delivery.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final PgClient pgClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public PaymentResponse pay(UUID customerId, PaymentRequest request) {
        // Check for duplicate payment with pessimistic lock
        paymentRepository.findByOrderIdWithLock(request.orderId()).ifPresent(p -> {
            throw BusinessException.conflict("이미 결제가 진행된 주문입니다.");
        });

        // Validate order via Feign
        OrderClient.OrderResponse order;
        try {
            order = orderClient.getOrder(request.orderId());
        } catch (Exception e) {
            throw BusinessException.notFound("주문을 찾을 수 없습니다.");
        }

        if (!order.customerId().equals(customerId)) {
            throw BusinessException.forbidden("해당 주문에 대한 권한이 없습니다.");
        }

        if (!"PENDING_PAYMENT".equals(order.status())) {
            throw BusinessException.badRequest("결제 대기 상태의 주문만 결제 가능합니다.");
        }

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .customerId(customerId)
                .amount(order.totalAmount())
                .method(request.method())
                .build();
        paymentRepository.save(payment);

        // Call PG
        PgResult pgResult = pgClient.charge(request.method(), order.totalAmount(), request.orderId().toString());

        if (pgResult.success()) {
            payment.complete(pgResult.transactionId());
            PaymentCompletedEvent event = new PaymentCompletedEvent(
                    payment.getId(), payment.getOrderId(), payment.getCustomerId(),
                    payment.getAmount(), payment.getPgTransactionId());
            kafkaTemplate.send("payment.completed", payment.getOrderId().toString(), event);
            log.info("Payment completed for orderId: {}", payment.getOrderId());
        } else {
            payment.fail();
            PaymentFailedEvent event = new PaymentFailedEvent(
                    payment.getId(), payment.getOrderId(), payment.getCustomerId(),
                    pgResult.errorMessage());
            kafkaTemplate.send("payment.failed", payment.getOrderId().toString(), event);
            log.warn("Payment failed for orderId: {}", payment.getOrderId());
        }

        return PaymentResponse.from(payment);
    }

    @Transactional
    public PaymentResponse refund(UUID customerId, UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> BusinessException.notFound("결제 내역을 찾을 수 없습니다."));

        if (!payment.getCustomerId().equals(customerId)) {
            throw BusinessException.forbidden("해당 결제에 대한 권한이 없습니다.");
        }

        if (payment.getStatus() != com.ansj.delivery.payment.domain.PaymentStatus.COMPLETED) {
            throw BusinessException.badRequest("완료된 결제만 환불 가능합니다.");
        }

        PgResult pgResult = pgClient.refund(payment.getPgTransactionId());
        if (pgResult.success()) {
            payment.refund();
            log.info("Refund completed for paymentId: {}", paymentId);
        } else {
            throw BusinessException.badRequest("환불 처리 중 오류가 발생했습니다: " + pgResult.errorMessage());
        }

        return PaymentResponse.from(payment);
    }

    public PaymentResponse getPaymentByOrder(UUID customerId, UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> BusinessException.notFound("결제 내역을 찾을 수 없습니다."));

        if (!payment.getCustomerId().equals(customerId)) {
            throw BusinessException.forbidden("해당 결제에 대한 권한이 없습니다.");
        }

        return PaymentResponse.from(payment);
    }
}
