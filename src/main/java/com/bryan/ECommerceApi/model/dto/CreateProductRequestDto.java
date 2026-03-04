package com.bryan.ECommerceApi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateProductRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @Positive
        @NotNull
        BigDecimal price,
        @PositiveOrZero
        @NotNull
        Long stock,
        @NotBlank
        String imageUrl,
        @NotBlank
        String category
) {
}
