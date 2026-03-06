package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.Order;
import com.bryan.ECommerceApi.model.enums.Status;

import java.math.BigDecimal;

public record CheckoutResponseDto(
        Long id,
        BigDecimal total,
        Status status,
        String clientSecret
) {
    public static CheckoutResponseDto fromEntity(Order order, String clientSecret){
        return new CheckoutResponseDto(
                order.getId(),
                order.getTotal(),
                order.getStatus(),
                clientSecret
        );
    }
}
