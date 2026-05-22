package com.ansj.delivery.payment.controller;

import com.ansj.delivery.common.response.ApiResponse;
import com.ansj.delivery.payment.dto.PaymentRequest;
import com.ansj.delivery.payment.dto.PaymentResponse;
import com.ansj.delivery.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @RequestHeader("X-User-Id") UUID customerId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("결제가 처리되었습니다.", paymentService.pay(customerId, request)));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable UUID paymentId) {
        return ResponseEntity.ok(ApiResponse.ok("환불이 처리되었습니다.", paymentService.refund(customerId, paymentId)));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(
            @RequestHeader("X-User-Id") UUID customerId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPaymentByOrder(customerId, orderId)));
    }
}
