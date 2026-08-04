package com.demo.email.consumer;

import com.demo.common.event.KafkaTopics;
import com.demo.common.event.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * CONSUMER của email-service.
 *
 * @KafkaListener: đánh dấu method này là điểm nhận message.
 * Spring tạo một listener container chạy vòng lặp:
 *   poll() -> deserialize -> gọi method này -> commit offset -> poll() tiếp...
 *
 * @Header: lấy metadata của message (partition, offset) để quan sát —
 * rất hữu ích khi học: bạn sẽ THẤY message nằm ở partition nào, offset bao nhiêu.
 */
@Component
public class PaymentCompletedConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentCompletedConsumer.class);

    @KafkaListener(topics = KafkaTopics.PAYMENT_COMPLETED)
    public void onPaymentCompleted(PaymentCompletedEvent event,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received {} [partition={}, offset={}]", event, partition, offset);
        // Thực tế: gọi SMTP/SendGrid... Demo: chỉ log.
        log.info("Send email success");
    }
}
