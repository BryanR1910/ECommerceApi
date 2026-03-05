package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.model.dto.CreateProductRequestDto;
import com.bryan.ECommerceApi.model.dto.ProductResponseDto;
import com.bryan.ECommerceApi.model.dto.UpdateProductRequestDto;
import com.bryan.ECommerceApi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable("id") Long id){
        ProductResponseDto response = productService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable
            ){
        return ResponseEntity.ok(productService.getAll(pageable));
    }


}
