package com.ansj.delivery.payment.pg;

import com.ansj.delivery.payment.domain.PaymentMethod;

public interface PgClient {

    PgResult charge(PaymentMethod method, int amount, String orderId);

    PgResult refund(String transactionId);
}
