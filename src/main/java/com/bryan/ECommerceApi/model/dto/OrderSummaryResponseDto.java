package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.Order;
import com.bryan.ECommerceApi.model.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponseDto(
        Long id,
        BigDecimal total,
        Status status,
        Instant createdAt
) {
    public static OrderSummaryResponseDto fromEntity(Order order){
        return new OrderSummaryResponseDto(
                order.getId(),
                order.getTotal(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
