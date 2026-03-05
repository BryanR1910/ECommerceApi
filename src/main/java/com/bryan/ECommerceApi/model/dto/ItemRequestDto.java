package com.bryan.ECommerceApi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ItemRequestDto(
        @PositiveOrZero
        @NotNull
        Long productId,
        @Positive
        @NotNull
        Long quantity
)
{
}
