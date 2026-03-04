package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.model.dto.CreateProductRequestDto;
import com.bryan.ECommerceApi.model.dto.ProductResponseDto;
import com.bryan.ECommerceApi.model.dto.UpdateProductRequestDto;
import com.bryan.ECommerceApi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody CreateProductRequestDto dto){
        ProductResponseDto productResponseDto = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateProductRequestDto dto){
        ProductResponseDto productResponseDto = productService.update(id, dto);
        return ResponseEntity.ok(productResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
