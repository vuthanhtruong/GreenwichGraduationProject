package com.demo.payment.producer;

import com.demo.common.event.KafkaTopics;
import com.demo.common.event.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * PRODUCER: nơi duy nhất trong hệ thống gửi message lên Kafka.
 *
 * kafkaTemplate.send(topic, key, value):
 *  - key = paymentId: Kafka hash(key) % 3 để chọn partition.
 *    Cùng key -> luôn cùng partition -> đảm bảo THỨ TỰ cho cùng 1 payment.
 *  - send() là BẤT ĐỒNG BỘ: trả về CompletableFuture ngay lập tức,
 *    message được gom vào batch và gửi ở background thread ("sender thread").
 *    Nhờ vậy API /payments không bị chậm vì chờ Kafka.
 */
@Component
public class PaymentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentCompletedEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED, event.getPaymentId(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        RecordMetadata meta = result.getRecordMetadata();
                        log.info("Published PaymentCompletedEvent paymentId={} -> topic={} partition={} offset={}",
                                event.getPaymentId(), meta.topic(), meta.partition(), meta.offset());
                    } else {
                        // Production: đẩy vào Dead Letter / retry / outbox thay vì chỉ log
                        log.error("Failed to publish PaymentCompletedEvent paymentId={}",
                                event.getPaymentId(), ex);
                    }
                });
    }
}
