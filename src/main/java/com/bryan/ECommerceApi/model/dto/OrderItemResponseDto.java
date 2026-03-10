package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long quantity,
        BigDecimal priceAtPurchase,
        ProductResponseDto product
) {
    public static OrderItemResponseDto fromEntity(OrderItem item) {
        return new OrderItemResponseDto(
                item.getId(),
                item.getQuantity(),
                item.getPriceAtPurchase(),
                ProductResponseDto.fromEntity(item.getProduct())
        );
    }
}
