package com.bryan.ECommerceApi.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(
        @Positive
        @NotNull
        Long quantity
) {
}
