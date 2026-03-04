package com.bryan.ECommerceApi.model.dto;

import com.bryan.ECommerceApi.model.Product;

import java.math.BigDecimal;

public record ProductResponseDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Long stock,
        String imageUrl,
        String category
) {
    public static ProductResponseDto fromEntity(Product product){
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory()
        );
    }
}
