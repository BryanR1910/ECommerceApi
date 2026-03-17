package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.exception.InsufficientStockException;
import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.CartItem;
import com.bryan.ECommerceApi.model.Product;
import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.dto.CartItemRequestDto;
import com.bryan.ECommerceApi.model.dto.CartItemResponseDto;
import com.bryan.ECommerceApi.repository.CartItemRepo;
import com.bryan.ECommerceApi.service.CartItemService;
import com.bryan.ECommerceApi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepo cartItemRepo;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartItemService cartItemService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    
    @BeforeEach
    void init(){
        testUser = new User(1L, "bryan", "bryan@gmail.com","encoded",false);
        testCart = new Cart(testUser);
        testProduct = new Product(1L,"keyboard","",new BigDecimal(100), 5L, "image.jpg","");
    }

    @Test
    void givenValidProductAndQuantity_whenAddNewItemToCart_thenItemIsAddedSuccessfully(){
        //arrange
        CartItemRequestDto request = new CartItemRequestDto(
                1L,
                2L
        );
        
        //act
        when(productService.findEntityById(request.productId())).thenReturn(testProduct);
        cartItemService.addItem(testCart, request);
        
        //assert
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepo).save(cartItemArgumentCaptor.capture());
        assertEquals(testProduct.getId(), cartItemArgumentCaptor.getValue().getProduct().getId());
        assertEquals(testCart.getId(), cartItemArgumentCaptor.getValue().getCart().getId());
        assertEquals(2L, cartItemArgumentCaptor.getValue().getQuantity());
    }

    @Test
    void givenQuantityGreaterThanStock_whenAddNewItemToCart_thenThrowsInsufficientStockException(){
        //arrange
        CartItemRequestDto request = new CartItemRequestDto(
                1L,
                6L // quantity > product stock (5)
        );
        
        //act - assert
        when(productService.findEntityById(request.productId())).thenReturn(testProduct);
        assertThrows(InsufficientStockException.class, () -> cartItemService.addItem(testCart, request));
        verify(cartItemRepo, never()).save(any());
    }

    @Test
    void givenExistingCartItem_whenAddSameProduct_thenQuantityIsUpdated(){
        //arrange
        CartItemRequestDto request = new CartItemRequestDto(
                1L,
                2L
        );
        CartItem existingCartItem = new CartItem(testCart, testProduct, 1L);
        testCart.setItems(List.of(existingCartItem));
        
        //act
        when(productService.findEntityById(request.productId())).thenReturn(testProduct);
        cartItemService.addItem(testCart, request);
        
        //assert
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepo).save(cartItemArgumentCaptor.capture());
        assertEquals(testProduct.getId(), cartItemArgumentCaptor.getValue().getProduct().getId());
        assertEquals(testCart.getId(), cartItemArgumentCaptor.getValue().getCart().getId());
        assertEquals(3L, cartItemArgumentCaptor.getValue().getQuantity()); // 1 + 2 = 3
    }

    @Test
    void givenTotalQuantityExceedsStock_whenUpdateExistingCartItem_thenThrowsInsufficientStockException(){
        //arrange
        CartItemRequestDto request = new CartItemRequestDto(
                1L,
                5L // existing (1) + new (5) = 6 > stock (5)
        );
        CartItem existingCartItem = new CartItem(testCart, testProduct, 1L);
        testCart.setItems(List.of(existingCartItem));
        
        //act - assert
        when(productService.findEntityById(request.productId())).thenReturn(testProduct);
        assertThrows(InsufficientStockException.class, () -> cartItemService.addItem(testCart, request));
        verify(cartItemRepo, never()).save(any());
    }

    @Test
    void givenValidCartItemId_whenDeleteItem_thenItemIsDeletedSuccessfully(){
        //arrange
        CartItem cartItem = new CartItem(testCart, testProduct, 1L);
        cartItem.setId(1L);

        //act
        when(cartItemRepo.findById(1L)).thenReturn(Optional.of(cartItem));
        cartItemService.deleteItem(cartItem.getId());
        
        //assert
        verify(cartItemRepo).delete(any(CartItem.class));
    }

    @Test
    void givenNonExistingCartItemId_whenDeleteItem_thenThrowsResourceNotFoundException(){
        //arrange
        
        //act - assert
        when(cartItemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartItemService.deleteItem(1L));
        verify(cartItemRepo, never()).delete(any());
    }

    @Test
    void givenValidItemIdAndQuantity_whenUpdateQuantityByCartItemId_thenQuantityIsUpdated(){
        //arrange
        CartItem cartItem = new CartItem(testCart, testProduct, 1L);
        CartItemResponseDto expectedResponse = CartItemResponseDto.fromEntity(cartItem);
        
        //act
        when(cartItemRepo.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepo.save(any(CartItem.class))).thenAnswer(invocation -> {
            CartItem cartItem1 = invocation.getArgument(0);
            return cartItem1;
        });
        
        CartItemResponseDto result = cartItemService.updateQuantityByCartItemId(1L, 3L);
        
        //assert
        assertNotNull(result);
        verify(cartItemRepo).save(any(CartItem.class));
        assertEquals(3L, result.quantity());
    }

    @Test
    void givenItemIdWithQuantityGreaterThanStock_whenUpdateQuantityByCartItemId_thenThrowsInsufficientStockException(){
        //arrange
        CartItem cartItem = new CartItem(testCart, testProduct, 1L);
        
        //act - assert
        when(cartItemRepo.findById(1L)).thenReturn(Optional.of(cartItem));
        assertThrows(InsufficientStockException.class, () -> cartItemService.updateQuantityByCartItemId(1L, 10L)); // 10 > stock (5)
        verify(cartItemRepo, never()).save(any());
    }

    @Test
    void givenNonExistingItemId_whenUpdateQuantityByCartItemId_thenThrowsResourceNotFoundException(){
        //arrange
        
        //act - assert
        when(cartItemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartItemService.updateQuantityByCartItemId(1L, 2L));
        verify(cartItemRepo, never()).save(any());
    }

}
