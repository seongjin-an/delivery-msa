package com.ansj.delivery.payment.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        String reason
) {}
