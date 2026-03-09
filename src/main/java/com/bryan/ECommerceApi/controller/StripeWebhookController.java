package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {
    private final OrderService orderService;
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    public StripeWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws SignatureVerificationException {
        Event event;

        event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

        switch (event.getType()){
            case    "payment_intent.succeeded" -> {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                orderService.markAsPaid(intent.getId());
            }
            case "payment_intent.payment_failed" -> {
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElseThrow();
                orderService.markAsFailed(intent.getId());
            }
            default -> System.out.println("Unhandled event type: " + event.getType());
        }
        return ResponseEntity.ok().build();
    }

}
