package com.ansj.delivery.notification.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        String reason
) {}
