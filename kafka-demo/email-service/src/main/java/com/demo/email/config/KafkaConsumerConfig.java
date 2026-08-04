package com.demo.email.config;

import java.util.HashMap;
import java.util.Map;

import com.demo.common.event.PaymentCompletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Cấu hình phía CONSUMER.
 *
 * @EnableKafka: bật cơ chế quét @KafkaListener. Không có annotation này,
 * @KafkaListener chỉ là method bình thường, không ai gọi nó.
 * (Spring Boot auto-configuration thực ra đã bật sẵn, nhưng khai báo tường minh
 * để hiểu rõ cơ chế.)
 *
 * ConsumerFactory -> tạo KafkaConsumer (client poll message từ broker).
 * ConcurrentKafkaListenerContainerFactory -> tạo "container" bao quanh consumer:
 *   container quản lý vòng lặp poll(), thread, commit offset, error handling...
 *   rồi giao message cho method @KafkaListener của bạn.
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, PaymentCompletedEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // Group ID: các consumer cùng group chia nhau partition;
        // các group khác nhau đều nhận đủ toàn bộ message.
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // Lần ĐẦU TIÊN group này kết nối (chưa có offset đã lưu):
        // "earliest" = đọc từ message cũ nhất còn trong topic.
        // Các lần sau luôn đọc tiếp từ offset đã commit.
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // JSON bytes -> PaymentCompletedEvent
        JsonDeserializer<PaymentCompletedEvent> jsonDeserializer =
                new JsonDeserializer<>(PaymentCompletedEvent.class);
        // Chỉ cho phép deserialize class trong package này (chống RCE qua type header)
        jsonDeserializer.addTrustedPackages("com.demo.common.event");

        // ErrorHandlingDeserializer: nếu gặp message rác (poison pill) không parse
        // được, consumer không chết + không loop vô hạn, mà chuyển cho error handler.
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, PaymentCompletedEvent> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent>();
        factory.setConsumerFactory(consumerFactory);
        // concurrency=3 = 3 consumer thread trong CÙNG group này,
        // khớp với 3 partition của topic -> mỗi thread phụ trách 1 partition.
        factory.setConcurrency(3);
        return factory;
    }
}
