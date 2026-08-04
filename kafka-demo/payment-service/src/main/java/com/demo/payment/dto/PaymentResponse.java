package com.demo.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.demo.payment.domain.Payment;

public record PaymentResponse(
        String paymentId,
        String orderId,
        String status,
        BigDecimal amount,
        String currency,
        Instant createdAt) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCreatedAt());
    }
}
