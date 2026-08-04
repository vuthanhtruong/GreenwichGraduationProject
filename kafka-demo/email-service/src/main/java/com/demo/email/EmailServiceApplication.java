package com.demo.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Service này KHÔNG có web server. App vẫn "sống" vì các thread của
 * Kafka listener container là non-daemon thread, chạy vòng lặp poll() liên tục.
 */
@SpringBootApplication
public class EmailServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailServiceApplication.class, args);
    }
}
