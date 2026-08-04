package com.demo.common.event;

/**
 * Tên topic được share qua common-event để Producer và Consumer
 * không bao giờ lệch nhau vì gõ nhầm chuỗi.
 */
public final class KafkaTopics {

    public static final String PAYMENT_COMPLETED = "payment-completed";

    private KafkaTopics() {
    }
}
