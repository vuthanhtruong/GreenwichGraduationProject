package com.demo.common.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event = "sự kiện đã xảy ra trong quá khứ" (immutable fact).
 *
 * Đây là CONTRACT giữa Producer (payment-service) và các Consumer
 * (email-service, notification-service, audit-service).
 *
 * Quy tắc đặt tên: <Danh từ> + <Động từ quá khứ> + Event
 * -> PaymentCompletedEvent = "Thanh toán ĐÃ hoàn tất".
 *
 * Lưu ý: cần constructor rỗng + getter/setter để Jackson (JSON) deserialize được.
 */
public class PaymentCompletedEvent {

    private String paymentId;
    private String orderId;
    private String customerEmail;
    private BigDecimal amount;
    private String currency;
    private Instant completedAt;

    public PaymentCompletedEvent() {
        // Bắt buộc cho Jackson JsonDeserializer
    }

    public PaymentCompletedEvent(String paymentId, String orderId, String customerEmail,
                                 BigDecimal amount, String currency, Instant completedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.currency = currency;
        this.completedAt = completedAt;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    @Override
    public String toString() {
        return "PaymentCompletedEvent{paymentId='%s', orderId='%s', customerEmail='%s', amount=%s %s, completedAt=%s}"
                .formatted(paymentId, orderId, customerEmail, amount, currency, completedAt);
    }
}
