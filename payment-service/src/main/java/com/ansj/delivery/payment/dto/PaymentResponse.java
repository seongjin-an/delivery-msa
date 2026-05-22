package com.ansj.delivery.payment.dto;

import com.ansj.delivery.payment.domain.Payment;
import com.ansj.delivery.payment.domain.PaymentMethod;
import com.ansj.delivery.payment.domain.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID customerId,
        int amount,
        PaymentMethod method,
        PaymentStatus status,
        String pgTransactionId,
        LocalDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(), payment.getOrderId(), payment.getCustomerId(),
                payment.getAmount(), payment.getMethod(), payment.getStatus(),
                payment.getPgTransactionId(), payment.getCreatedAt());
    }
}
