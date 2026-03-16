package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.exception.InsufficientStockException;
import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Product;
import com.bryan.ECommerceApi.model.dto.CreateProductRequestDto;
import com.bryan.ECommerceApi.model.dto.ProductResponseDto;
import com.bryan.ECommerceApi.model.dto.UpdateProductRequestDto;
import com.bryan.ECommerceApi.repository.ProductRepo;
import com.bryan.ECommerceApi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private CreateProductRequestDto createDto;
    private UpdateProductRequestDto updateDto;
    private ProductResponseDto productResponseDto;

    @BeforeEach
    void init() {
        product = new Product(1L,"Test Product", "Description", BigDecimal.valueOf(100), 10L, "image.jpg", "Category");

        createDto = new CreateProductRequestDto(
                "Test Product",
                "Description",
                BigDecimal.valueOf(100),
                10L,
                "image.jpg",
                "Category"
        );

        updateDto = new UpdateProductRequestDto(
                "Updated Product",
                "Updated Description",
                BigDecimal.valueOf(150),
                20L,
                "updated.jpg",
                "Updated Category"
        );

        productResponseDto = ProductResponseDto.fromEntity(product);
    }

    @Test
    void givenValidData_whenCreate_thenReturnsProductResponseDto() {
        // Arrange
        when(productRepo.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponseDto result = productService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
        assertEquals(product.getPrice(), result.price());
        assertEquals(product.getStock(), result.stock());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void givenValidIdAndQuantity_whenReduceStock_thenDoesNotThrowException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(productRepo.reduceStock(1L, 5L)).thenReturn(1); // 1 row updated

        // Act & Assert
        assertDoesNotThrow(() -> productService.reduceStock(1L, 5L));
        verify(productRepo, times(1)).reduceStock(1L, 5L);
    }

    @Test
    void givenInsufficientStock_whenReduceStock_thenThrowsInsufficientStockException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(productRepo.reduceStock(1L, 15L)).thenReturn(0); // 0 rows updated (insufficient stock)

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> productService.reduceStock(1L, 15L));
        verify(productRepo, times(1)).reduceStock(1L, 15L);
    }

    @Test
    void givenNonExistingId_whenReduceStock_thenThrowsResourceNotFoundException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.reduceStock(1L, 5L));
        verify(productRepo, never()).reduceStock(anyLong(), anyLong());
    }

    @Test
    void givenValidData_whenUpdate_thenReturnsUpdatedProductResponseDto() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponseDto result = productService.update(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateDto.name(), result.name());
        assertEquals(updateDto.description(), result.description());
        assertEquals(updateDto.price(), result.price());
        assertEquals(updateDto.stock(), result.stock());
        assertEquals(updateDto.imageUrl(), result.imageUrl());
        assertEquals(updateDto.category(), result.category());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void givenNonExistingId_whenUpdate_thenThrowsResourceNotFoundException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.update(1L, updateDto));
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void givenValidId_whenDelete_thenDoesNotThrowException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertDoesNotThrow(() -> productService.delete(1L));
        verify(productRepo, times(1)).delete(any(Product.class));
    }

    @Test
    void givenNonExistingId_whenDelete_thenThrowsResourceNotFoundException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.delete(1L));
        verify(productRepo, never()).delete(any(Product.class));
    }

    @Test
    void givenValidId_whenFindById_thenReturnsProductResponseDto() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponseDto result = productService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
        verify(productRepo, times(1)).findById(1L);
    }

    @Test
    void givenNonExistingId_whenFindById_thenThrowsResourceNotFoundException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.findById(1L));
        verify(productRepo, times(1)).findById(1L);
    }

    @Test
    void whenGetAll_thenReturnsPageOfProductResponseDto() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));
        when(productRepo.findAll(any(Pageable.class))).thenReturn(productPage);

        // Act
        Page<ProductResponseDto> result = productService.getAll(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(product.getId(), result.getContent().getFirst().id());
        verify(productRepo, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void givenValidId_whenFindEntityById_thenReturnsProduct() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        verify(productRepo, times(1)).findById(1L);
    }

    @Test
    void givenNonExistingId_whenFindEntityById_thenThrowsResourceNotFoundException() {
        // Arrange
        when(productRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.findEntityById(1L));
        verify(productRepo, times(1)).findById(1L);
    }

    @Test
    void givenNullFieldsInUpdateDto_whenUpdate_thenDoesNotUpdateNullFields() {
        // Arrange
        Product existingProduct = new Product(1L, "Original Name", "Original Description", BigDecimal.valueOf(50), 5L, "original.jpg", "Original Category");
        when(productRepo.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return savedProduct;
        });

        // Create update DTO with some null fields
        UpdateProductRequestDto updateDtoWithNulls = new UpdateProductRequestDto(
            null, // name is null
                null, // description is not null
                null, // price is null
                null, // stock is null
                null, // imageUrl is not null
                null // category is null
        );

        // Act
        ProductResponseDto result = productService.update(1L, updateDtoWithNulls);

        // Assert
        assertNotNull(result);
        // Fields that were null in DTO should remain unchanged
        assertEquals("Original Name", result.name()); // unchanged
        assertEquals("Original Description", result.description()); // updated
        assertEquals(BigDecimal.valueOf(50), result.price()); // unchanged
        assertEquals(5L, result.stock()); // unchanged
        assertEquals("original.jpg", result.imageUrl()); // updated
        assertEquals("Original Category", result.category()); // unchanged
        
        // Verify save was called
        verify(productRepo, times(1)).save(any(Product.class));
    }
}