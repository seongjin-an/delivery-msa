package com.ansj.delivery.payment.domain;

import com.ansj.delivery.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private UUID customerId;

    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String pgTransactionId;

    @Builder
    public Payment(UUID orderId, UUID customerId, int amount, PaymentMethod method) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public void complete(String pgTransactionId) {
        this.pgTransactionId = pgTransactionId;
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }
}
