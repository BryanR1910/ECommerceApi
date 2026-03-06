package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.model.dto.CheckoutResponseDto;
import com.bryan.ECommerceApi.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("checkout")
    public ResponseEntity<CheckoutResponseDto> checkout(Authentication authentication) throws StripeException {
        String email = authentication.getName();
        CheckoutResponseDto response = orderService.checkout(email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
