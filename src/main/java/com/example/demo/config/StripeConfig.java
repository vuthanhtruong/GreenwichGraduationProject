package com.example.demo.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.secretKey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        System.out.println("Stripe Secret Key Loaded: " + secretKey);
        Stripe.apiKey = secretKey;
    }

}
