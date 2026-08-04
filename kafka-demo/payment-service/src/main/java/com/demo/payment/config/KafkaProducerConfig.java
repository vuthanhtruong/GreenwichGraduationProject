package com.demo.payment.config;

import java.util.HashMap;
import java.util.Map;

import com.demo.common.event.KafkaTopics;
import com.demo.common.event.PaymentCompletedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Cấu hình phía PRODUCER.
 *
 * ProducerFactory  -> "nhà máy" tạo ra KafkaProducer (client nói chuyện với broker).
 * KafkaTemplate    -> lớp tiện ích của Spring bọc quanh KafkaProducer,
 *                     tương tự JdbcTemplate/RestTemplate: bạn chỉ gọi send(),
 *                     mọi việc quản lý connection, batching, retry... nó lo.
 * NewTopic         -> khai báo topic dạng code; Spring Boot tự tạo bean KafkaAdmin,
 *                     KafkaAdmin thấy bean NewTopic sẽ tạo topic trên broker lúc khởi động
 *                     (nếu topic chưa tồn tại).
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, PaymentCompletedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        // Danh sách broker để client kết nối lần đầu (sau đó client tự discover cả cluster)
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Key là String (paymentId) -> quyết định message rơi vào partition nào
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Value là object Java -> serialize thành JSON bytes trước khi gửi lên broker
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // acks=all: broker chỉ trả "thành công" khi message đã được ghi bền vững.
        // Với cluster nhiều broker nghĩa là đã replicate đủ ISR -> không mất message.
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // Idempotent producer: retry không tạo message trùng (exactly-once per partition)
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate(
            ProducerFactory<String, PaymentCompletedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Topic "payment-completed" với 3 partition.
     * replicas=1 vì demo chỉ có 1 broker; production thường replicas=3.
     */
    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENT_COMPLETED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
