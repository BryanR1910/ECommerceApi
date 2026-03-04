package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Product;
import com.bryan.ECommerceApi.model.dto.CreateProductRequestDto;
import com.bryan.ECommerceApi.model.dto.ProductResponseDto;
import com.bryan.ECommerceApi.model.dto.UpdateProductRequestDto;
import com.bryan.ECommerceApi.repository.ProductRepo;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public ProductResponseDto create(CreateProductRequestDto dto){
        Product product = new Product(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.stock(),
                dto.imageUrl(),
                dto.category()
        );
        Product savedProduct = productRepo.save(product);
        return ProductResponseDto.fromEntity(savedProduct);
    }

    public ProductResponseDto update(Long id, UpdateProductRequestDto dto){
        Product product = productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (dto.name() != null) product.setName(dto.name());
        if (dto.description() != null) product.setDescription(dto.description());
        if (dto.price() != null) product.setPrice(dto.price());
        if (dto.stock() != null) product.setStock(dto.stock());
        if (dto.imageUrl() != null) product.setImageUrl(dto.imageUrl());
        if (dto.category() != null) product.setCategory(dto.category());

        Product updatedProduct = productRepo.save(product);
        return ProductResponseDto.fromEntity(updatedProduct);
    }

    public void delete(Long id){
        Product product = productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        productRepo.delete(product);
    }



}
