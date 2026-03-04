package com.bryan.ECommerceApi.model.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateProductRequestDto(
        String name,
        String description,
        @Positive
        BigDecimal price,
        @PositiveOrZero
        Long stock,
        String imageUrl,
        String category
) {
}
