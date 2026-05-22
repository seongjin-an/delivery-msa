package com.ansj.delivery.payment.pg;

public record PgResult(boolean success, String transactionId, String errorMessage) {}
