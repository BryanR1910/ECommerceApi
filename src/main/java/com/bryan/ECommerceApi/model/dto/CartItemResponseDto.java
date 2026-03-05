package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.CartItem;

public record CartItemResponseDto(
        Long id,
        ProductResponseDto product,
        Long quantity
) {
    public static CartItemResponseDto fromEntity(CartItem item) {
        return new CartItemResponseDto(
                item.getId(),
                ProductResponseDto.fromEntity(item.getProduct()),
                item.getQuantity()
        );
    }
}
