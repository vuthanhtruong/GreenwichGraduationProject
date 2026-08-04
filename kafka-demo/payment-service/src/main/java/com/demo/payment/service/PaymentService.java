package com.demo.payment.service;

import java.time.Instant;
import java.util.UUID;

import com.demo.common.event.PaymentCompletedEvent;
import com.demo.payment.domain.Payment;
import com.demo.payment.dto.PaymentRequest;
import com.demo.payment.producer.PaymentEventProducer;
import com.demo.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentEventProducer paymentEventProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentEventProducer = paymentEventProducer;
    }

    /**
     * 1. Lưu Payment vào DB (trong transaction).
     * 2. Publish PaymentCompletedEvent lên Kafka.
     *
     * Lưu ý senior: đây là "dual-write" (ghi 2 hệ thống DB + Kafka không chung
     * transaction). Nếu DB commit xong mà app chết trước khi Kafka gửi được thì
     * event bị mất. Production giải quyết bằng Transactional Outbox Pattern
     * (xem README, mục nâng cao). Demo này giữ đơn giản để tập trung học Kafka.
     */
    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                request.orderId(),
                request.customerEmail(),
                request.amount(),
                request.currency(),
                "COMPLETED",
                Instant.now());
        paymentRepository.save(payment);

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerEmail(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCreatedAt());
        paymentEventProducer.publish(event);

        return payment;
    }
}
