package com.demo.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String orderId,
        String customerEmail,
        BigDecimal amount,
        String currency) {
}
