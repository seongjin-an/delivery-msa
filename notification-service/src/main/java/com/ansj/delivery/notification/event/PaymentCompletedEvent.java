package com.ansj.delivery.notification.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        int amount
) {}
