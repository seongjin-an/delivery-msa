package com.ansj.delivery.payment.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        int amount,
        String pgTransactionId
) {}
