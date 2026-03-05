package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.Cart;

import java.time.Instant;
import java.util.List;

public record CartResponseDto(
        Long id,
        Instant createdAt,
        List<CartItemResponseDto> items

) {
    public static CartResponseDto fromEntity(Cart cart){
        return new CartResponseDto(
                cart.getId(),
                cart.getCreatedAt(),
                cart.getItems().stream().map(CartItemResponseDto::fromEntity).toList()
        );
    }
}
