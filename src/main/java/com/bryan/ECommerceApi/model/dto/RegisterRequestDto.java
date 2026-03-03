package com.bryan.ECommerceApi.model.dto;

public record RegisterRequestDto(
        String name,
        String email,
        String password,
        boolean isAdmin
) {
}
