package com.ansj.delivery.payment.pg;

import com.ansj.delivery.payment.domain.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 실제 PG 연동 없이 항상 성공 응답을 반환하는 Fake 구현체.
 * 개발/테스트 환경에서 사용한다.
 */
@Slf4j
@Component
public class FakePgClient implements PgClient {

    @Override
    public PgResult charge(PaymentMethod method, int amount, String orderId) {
        String txId = "FAKE-" + UUID.randomUUID();
        log.info("[FakePG] Charge approved: method={}, amount={}, orderId={}, txId={}", method, amount, orderId, txId);
        return new PgResult(true, txId, null);
    }

    @Override
    public PgResult refund(String transactionId) {
        log.info("[FakePG] Refund approved: transactionId={}", transactionId);
        return new PgResult(true, transactionId, null);
    }
}
