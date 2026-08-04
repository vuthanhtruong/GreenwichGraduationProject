# Kafka Microservices Demo — Học Kafka từ cơ bản đến nâng cao

Hệ thống thanh toán event-driven gồm 4 microservice độc lập + 1 thư viện chung,
giao tiếp **duy nhất qua Kafka**, không service nào gọi REST tới service nào.

## 1. Sơ đồ kiến trúc

```
                                  ┌──────────────────────┐
                                  │      PostgreSQL      │
                                  │     (paymentdb)      │
                                  └──────────▲───────────┘
                                             │ (2) INSERT payment
                                             │
 ┌────────┐  (1) POST /payments   ┌──────────┴───────────┐
 │ Client ├──────────────────────►│   payment-service    │
 └────────┘  (5) 201 Created      │      [PRODUCER]      │
                                  └──────────┬───────────┘
                                             │ (3) publish PaymentCompletedEvent
                                             │     key = paymentId
                                             ▼
                    ┌────────────────────────────────────────────┐
                    │              KAFKA BROKER                  │
                    │        topic: "payment-completed"          │
                    │  ┌───────────┐ ┌───────────┐ ┌───────────┐ │
                    │  │partition 0│ │partition 1│ │partition 2│ │
                    │  │ 0│1│2│... │ │ 0│1│2│... │ │ 0│1│2│... │ │  ◄─ offset
                    │  └───────────┘ └───────────┘ └───────────┘ │
                    └─────┬───────────────┬───────────────┬──────┘
                          │ (4) poll      │ (4) poll      │ (4) poll
                          ▼               ▼               ▼
                 ┌────────────────┐ ┌────────────────┐ ┌────────────────┐
                 │ email-service  │ │ notification-  │ │ audit-service  │
                 │   [CONSUMER]   │ │    service     │ │   [CONSUMER]   │
                 │                │ │   [CONSUMER]   │ │                │
                 │ group:         │ │ group:         │ │ group:         │
                 │ email-service- │ │ notification-  │ │ audit-service- │
                 │     group      │ │ service-group  │ │     group      │
                 └────────────────┘ └────────────────┘ └────────────────┘
                  "Send email        "Push notification  "Audit saved"
                     success"            success"
```

3 group ID **khác nhau** ⇒ Kafka gửi **mỗi** event cho **cả 3** service (fan-out / pub-sub).

## 2. Cấu trúc project

```
kafka-demo/
├── pom.xml                  # aggregator: build cả 5 module bằng 1 lệnh
├── docker-compose.yml       # kafka + kafka-ui + postgres + 4 service
├── common-event/            # thư viện chung (contract), KHÔNG phải service
│   └── PaymentCompletedEvent, KafkaTopics
├── payment-service/         # PRODUCER  — REST API + DB + publish event  (port 8080)
├── email-service/           # CONSUMER  — group: email-service-group
├── notification-service/    # CONSUMER  — group: notification-service-group
└── audit-service/           # CONSUMER  — group: audit-service-group
```

| Service | Vai trò | REST API | DB | Kafka |
|---|---|---|---|---|
| payment-service | **Producer** | `POST /payments` | PostgreSQL | `KafkaTemplate.send()` |
| email-service | **Consumer** | ✗ | ✗ | `@KafkaListener` |
| notification-service | **Consumer** | ✗ | ✗ | `@KafkaListener` |
| audit-service | **Consumer** | ✗ | ✗ | `@KafkaListener` |
| common-event | Shared contract | ✗ | ✗ | chứa event class + tên topic |

## 3. Chạy hệ thống

### Cách A — tất cả trong Docker (khuyến nghị lần đầu)

```bash
cd kafka-demo
docker compose up --build -d
docker compose logs -f email-service notification-service audit-service
```

### Cách B — hạ tầng trong Docker, service chạy local (tiện debug)

```bash
docker compose up -d kafka kafka-ui postgres
mvn install -DskipTests            # build common-event vào local repo
# mở 4 terminal:
mvn -pl payment-service spring-boot:run
mvn -pl email-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl audit-service spring-boot:run
```

### Test

```bash
curl -X POST http://localhost:8080/payments -H "Content-Type: application/json" -d "{\"orderId\":\"ORD-001\",\"customerEmail\":\"user@example.com\",\"amount\":250000,\"currency\":\"VND\"}"
```

Kết quả: 3 consumer cùng log event, kèm `Send email success` / `Push notification success` / `Audit saved`.

**Kafka UI**: http://localhost:8085 — xem topic, partition, offset, consumer group bằng mắt.

## 4. Producer nằm ở đâu? Consumer nằm ở đâu?

- **Producer** nằm trong `payment-service`, cụ thể là class `PaymentEventProducer`
  (`payment-service/src/main/java/com/demo/payment/producer/PaymentEventProducer.java`).
  Nó là nơi **duy nhất** gọi `kafkaTemplate.send(...)`. Producer không phải một
  "server" riêng — nó chỉ là một **Kafka client** sống bên trong JVM của payment-service,
  mở TCP connection tới broker.

- **Consumer** nằm trong 3 service còn lại, cụ thể là method có `@KafkaListener`
  trong class `PaymentCompletedConsumer` của từng service. Tương tự, consumer cũng
  chỉ là một Kafka client sống trong JVM của service đó, liên tục `poll()` broker.

- **Kafka Broker** là process đứng giữa (container `kafka` trong docker-compose).
  Producer và Consumer **không bao giờ nói chuyện trực tiếp với nhau** — cả hai chỉ
  nói chuyện với broker.

## 5. Kafka Broker hoạt động như thế nào?

1. Broker nhận message từ producer và **ghi xuống disk** (append-only log, tuần tự —
   vì ghi tuần tự nên rất nhanh). Message KHÔNG bị xóa sau khi consumer đọc; nó được
   giữ theo retention (mặc định 7 ngày).
2. Mỗi topic được chia thành **partition**; mỗi partition là một log độc lập.
   Broker gán cho mỗi message trong partition một số thứ tự tăng dần: **offset**.
3. Với consumer, broker đóng vai trò "thư viện": consumer tự đến **poll** (kéo dữ liệu về),
   broker không push. Broker chỉ nhớ giúp mỗi group đã đọc đến offset nào
   (lưu trong topic nội bộ `__consumer_offsets`).
4. Broker còn chạy **Group Coordinator**: chia partition cho các consumer trong cùng
   group, và **rebalance** (chia lại) khi có consumer vào/ra khỏi group.
5. Từ Kafka 3.x, broker dùng **KRaft** (tự quản lý metadata bằng Raft) — không cần
   Zookeeper nữa. Trong compose, `KAFKA_PROCESS_ROLES: broker,controller` nghĩa là
   1 node kiêm cả hai vai trò.

## 6. Vì sao các service không gọi REST tới nhau mà vẫn hoạt động?

Vì chúng giao tiếp **bất đồng bộ qua trung gian** (message broker) thay vì đồng bộ
điểm-nối-điểm:

- payment-service chỉ cần biết **địa chỉ broker** và **tên topic**. Nó không biết
  (và không cần biết) ai sẽ đọc event. Gửi xong là xong việc — *fire and forget*.
- 3 consumer chỉ cần biết broker + topic. Chúng không biết ai đã gửi.
- Cái hai bên chia sẻ là **contract**: class `PaymentCompletedEvent` trong `common-event`.

Hệ quả (đây chính là lý do dùng Kafka):

| | REST giữa các service | Kafka |
|---|---|---|
| email-service chết 10 phút | Thanh toán fail hoặc mất email | Thanh toán vẫn OK; event chờ trong topic, service sống lại đọc tiếp từ offset cũ |
| Thêm service mới (vd: loyalty-service) | Phải sửa code payment-service để gọi thêm 1 REST call | Chỉ cần subscribe topic với group mới — payment-service không đổi 1 dòng |
| Response time của /payments | = tổng thời gian của cả 3 service | ≈ thời gian ghi DB + gửi broker (vài ms) |
| Coupling | payment biết địa chỉ, API, format của cả 3 service | payment chỉ biết broker + contract |

## 7. Vòng đời của một message (từ Client đến khi tất cả consumer xử lý xong)

```
(1)  Client ──POST /payments──► PaymentController
(2)  PaymentService: mở transaction, INSERT bảng payments, commit
(3)  PaymentEventProducer.publish():
     3a. JsonSerializer: PaymentCompletedEvent (Java object) ──► JSON bytes
     3b. Partitioner: hash(key=paymentId) % 3 ──► chọn partition, vd partition 1
     3c. Message vào buffer, sender thread (background) gom batch gửi tới broker
(4)  Broker (leader của partition 1):
     4a. Append message vào cuối log của partition 1, gán offset (vd offset=41)
     4b. Vì acks=all + đủ ISR ──► trả ack về producer
     4c. Producer callback chạy: log "partition=1 offset=41"
(5)  Controller trả 201 Created cho Client
     ─── Client XONG tại đây. Mọi thứ phía dưới xảy ra SAU, độc lập ───
(6)  3 consumer (3 group khác nhau) đang poll():
     6a. email-service-group        nhận message tại partition 1, offset 41
     6b. notification-service-group nhận CÙNG message đó (offset của group này)
     6c. audit-service-group        nhận CÙNG message đó
(7)  Mỗi consumer: JsonDeserializer bytes ──► PaymentCompletedEvent
     ──► gọi method @KafkaListener ──► log "... success"
(8)  Listener container commit offset: mỗi group ghi "đã xử lý xong đến offset 41
     của partition 1" vào __consumer_offsets
(9)  Message VẪN nằm trong topic (đến hết retention) — group mới sau này
     vẫn có thể đọc lại từ đầu
```

Chú ý bước (5) đứng **trước** bước (6): client nhận response trước khi email được
"gửi". Đó là bản chất của xử lý bất đồng bộ.

## 8. Consumer Group / Topic / Partition / Offset — giải thích bằng chính dự án này

### Topic — `payment-completed`
Kênh dữ liệu có tên, giống "category" của message. Producer ghi vào topic,
consumer đăng ký (subscribe) topic. Được tạo bởi bean `NewTopic` trong
`KafkaProducerConfig` với **3 partition**.

### Partition — topic này có 3
Mỗi partition là một append-only log độc lập, có thể nằm trên các broker khác nhau
→ đây là cách Kafka **scale ngang**. Message có key `paymentId`:
`hash(paymentId) % 3` quyết định partition. Hệ quả quan trọng:
- Kafka chỉ đảm bảo **thứ tự trong 1 partition**, không đảm bảo giữa các partition.
- Cùng key → luôn cùng partition → mọi event của cùng 1 payment được xử lý đúng thứ tự.

### Offset — "số trang sách đang đọc dở"
Số thứ tự của message trong partition (0, 1, 2, ...). Mỗi **group** tự bookmark
riêng: `email-service-group` có thể đang ở offset 41 trong khi
`audit-service-group` mới ở offset 30 (nếu audit chậm) — không ảnh hưởng nhau.
Khi restart, service đọc tiếp từ offset đã commit → không mất, không đọc trùng
(ở mức at-least-once). Trong log consumer bạn thấy chính xác `partition=X offset=Y`
của từng message.

### Consumer Group — cơ chế quyết định "chia nhau" hay "cùng nhận"
Quy tắc duy nhất cần nhớ:

> **Trong 1 group, mỗi partition chỉ giao cho đúng 1 consumer.
> Các group khác nhau độc lập hoàn toàn — group nào cũng nhận đủ mọi message.**

Áp vào dự án:
- 3 service dùng 3 group **khác nhau** → mỗi event được cả 3 service xử lý (pub-sub).
- Trong mỗi service, `factory.setConcurrency(3)` tạo 3 consumer thread **cùng group**
  → mỗi thread giữ 1 partition → xử lý song song (load balancing trong group).
- Thí nghiệm: chạy `docker compose up -d --scale email-service=2` (bỏ container_name
  trước) → 2 instance email-service cùng group chia nhau 3 partition, mỗi email chỉ
  được gửi 1 lần. Đó là cách scale consumer trong production.
- Số consumer hữu ích tối đa trong 1 group = số partition (consumer thừa sẽ ngồi không).

## 9. Giải thích các class & annotation chính

### payment-service (Producer)
| Class | Vai trò |
|---|---|
| `PaymentController` | REST endpoint `POST /payments`, điểm vào duy nhất từ client |
| `PaymentService` | Nghiệp vụ: lưu DB rồi nhờ producer publish event |
| `PaymentEventProducer` | Gọi `kafkaTemplate.send(topic, key, value)` — bất đồng bộ, trả `CompletableFuture` |
| `KafkaProducerConfig` | `ProducerFactory` (serializer, acks, idempotence), `KafkaTemplate`, `NewTopic` |
| `Payment` / `PaymentRepository` | JPA entity + repository lưu PostgreSQL |

### email/notification/audit-service (Consumer) — cấu trúc giống nhau
| Class | Vai trò |
|---|---|
| `KafkaConsumerConfig` | `@EnableKafka`, `ConsumerFactory` (deserializer, group-id, auto-offset-reset), `ConcurrentKafkaListenerContainerFactory` |
| `PaymentCompletedConsumer` | Method `@KafkaListener` nhận event và xử lý |

### Annotation / API
- **`KafkaTemplate`** — tương đương `JdbcTemplate` cho Kafka: bọc `KafkaProducer`,
  quản lý connection/batching/retry, expose `send()` đơn giản. Thread-safe, 1 bean dùng chung.
- **`@KafkaListener(topics = ...)`** — biến 1 method thường thành message handler.
  Spring tạo listener container chạy vòng lặp `poll() → deserialize → gọi method → commit offset`.
- **`@EnableKafka`** — bật hạ tầng xử lý `@KafkaListener` (bean post-processor quét
  annotation và tạo container). Spring Boot auto-config đã bật sẵn, khai báo tường minh
  để hiểu cơ chế.
- **`ProducerFactory` / `ConsumerFactory`** — factory tạo Kafka client thật sự
  (`KafkaProducer` / `KafkaConsumer`) với config: serializer, `acks`, `group.id`...
- **`ConcurrentKafkaListenerContainerFactory`** — tạo container cho mỗi `@KafkaListener`;
  `setConcurrency(3)` = 3 thread cùng group, mỗi thread 1 partition.
- **`JsonSerializer` / `JsonDeserializer`** — Kafka chỉ chở **bytes**; cặp này chuyển
  object ↔ JSON bytes. `addTrustedPackages` chặn deserialize class lạ (an toàn).
- **`ErrorHandlingDeserializer`** — bọc deserializer: gặp message rác (poison pill)
  thì không làm consumer chết / loop vô hạn.
- **`NewTopic` + `KafkaAdmin`** — Boot tự tạo `KafkaAdmin` từ `spring.kafka.bootstrap-servers`;
  KafkaAdmin thấy bean `NewTopic` sẽ tạo topic lúc khởi động nếu chưa có.
- **`acks=all` + `enable.idempotence=true`** — broker chỉ ack khi ghi bền vững;
  retry không sinh message trùng.
- **`auto.offset.reset=earliest`** — group mới toanh (chưa có offset) đọc từ message
  cũ nhất; các lần sau luôn đọc tiếp từ offset đã commit.

## 10. Chủ đề nâng cao để học tiếp (thứ tự gợi ý)

1. **Dual-write & Transactional Outbox** — trong `PaymentService`, DB commit và Kafka
   send là 2 hệ thống khác nhau: nếu app chết giữa chừng thì event mất. Production
   giải bằng outbox table + Debezium/CDC hoặc polling publisher.
2. **At-least-once & Idempotent consumer** — consumer xử lý xong nhưng chết trước khi
   commit offset → message được giao lại → consumer phải idempotent (vd: check
   paymentId đã xử lý chưa).
3. **Retry & Dead Letter Topic** — `DefaultErrorHandler` + `DeadLetterPublishingRecoverer`
   của Spring Kafka: fail N lần thì đẩy sang topic `payment-completed.DLT`.
4. **Rebalance** — bật/tắt consumer và quan sát log `partitions assigned/revoked`.
5. **Schema evolution** — thêm field vào event: nhờ `FAIL_ON_UNKNOWN_PROPERTIES=false`
   consumer cũ không chết. Chuẩn hơn: Avro/Protobuf + Schema Registry.
6. **Kafka transactions** (`isolation.level=read_committed`) và **exactly-once** với
   Kafka Streams.
7. **Compacted topic** (`cleanup.policy=compact`) — giữ giá trị mới nhất theo key.

## 11. Lệnh CLI hữu ích (chạy trong container kafka)

```bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic payment-completed
```

```bash
docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group email-service-group
```

```bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic payment-completed --from-beginning
```

Lệnh `kafka-consumer-groups.sh --describe` cho thấy **LAG** = (offset mới nhất trong
partition) − (offset group đã commit) — chỉ số quan trọng nhất khi vận hành Kafka.
