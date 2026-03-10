package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.Order;
import com.bryan.ECommerceApi.model.enums.Status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDto(
        Long id,
        BigDecimal total,
        Status status,
        Instant createdAt,
        List<OrderItemResponseDto> items
) {
    public static OrderResponseDto fromEntity(Order order){
        return new OrderResponseDto(
                order.getId(),
                order.getTotal(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponseDto::fromEntity).toList()
        );
    }
}
