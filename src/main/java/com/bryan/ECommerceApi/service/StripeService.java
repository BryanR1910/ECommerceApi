package com.bryan.ECommerceApi.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    public String createPaymentIntent(BigDecimal total, String currency) throws StripeException {
        Stripe.apiKey = secretKey;

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(total.multiply(BigDecimal.valueOf(100)).longValue())
                        .setCurrency(currency)
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }

    public String updatePaymentIntent(String paymentIntentId, BigDecimal total) throws StripeException {
        Stripe.apiKey = secretKey;

        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);

        PaymentIntentUpdateParams params = PaymentIntentUpdateParams.builder()
                .setAmount(total.multiply(BigDecimal.valueOf(100)).longValue())
                .build();

        return intent.update(params).getClientSecret();
    }

}
