package com.example.demo.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentController {

    @Value("${stripe.successUrl}")
    private String successUrl;

    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    @GetMapping("/payment/create")
    public String createCheckoutSession(@RequestParam("amount") long amount) throws StripeException {

        // amount tính bằng cents → 10000 VNĐ = 10000 * 100 = 1_000_000 VND cents
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl)
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd") // hoặc "vnd"
                                                        .setUnitAmount(amount * 100) // cents
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Deposit")
                                                                        .build())
                                                        .build())
                                        .build())
                        .build();

        Session session = Session.create(params);

        return "redirect:" + session.getUrl();
    }

    @GetMapping("/payment/success")
    public String success() {
        return "payment-success"; // template hiển thị kết quả
    }

    @GetMapping("/payment/cancel")
    public String cancel() {
        return "payment-cancel";
    }
}
