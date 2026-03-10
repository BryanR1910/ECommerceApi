package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.model.dto.CheckoutResponseDto;
import com.bryan.ECommerceApi.model.dto.OrderResponseDto;
import com.bryan.ECommerceApi.model.dto.OrderSummaryResponseDto;
import com.bryan.ECommerceApi.service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping()
    public ResponseEntity<Page<OrderSummaryResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Authentication authentication
            ){
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getAll(pageable, email));
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderResponseDto> getById(Authentication authentication, @PathVariable("id") Long id){
        String email = authentication.getName();
        OrderResponseDto response = orderService.getById(email, id);

        return ResponseEntity.ok(response);
    }

}
