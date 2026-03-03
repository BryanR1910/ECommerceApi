package com.bryan.ECommerceApi.model.dto;

public record LoginRequestDto(
        String email,
        String password
) {
}
