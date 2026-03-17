package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.CartItem;
import com.bryan.ECommerceApi.model.Product;
import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.dto.CartItemRequestDto;
import com.bryan.ECommerceApi.model.dto.CartItemResponseDto;
import com.bryan.ECommerceApi.model.dto.CartResponseDto;
import com.bryan.ECommerceApi.model.dto.UpdateCartItemRequestDto;
import com.bryan.ECommerceApi.repository.CartRepo;
import com.bryan.ECommerceApi.service.CartItemService;
import com.bryan.ECommerceApi.service.CartService;
import com.bryan.ECommerceApi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepo cartRepo;

    @Mock
    private UserService userService;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void init() {
        testUser = new User(1L, "bryan", "bryan@gmail.com", "encoded", false);
        testCart = new Cart(testUser);
        testCart.setId(1L);
        
        testProduct = new Product(1L, "keyboard", "", new BigDecimal(100), 5L, "image.jpg", "");
        
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2L);
    }

    @Test
    void givenValidUser_whenCreate_thenCartIsCreatedSuccessfully() {
        //arrange
        
        //act
        cartService.create(testUser);
        
        //assert
        verify(cartRepo).save(any(Cart.class));
    }

    @Test
    void givenValidUserEmail_whenGetByUserEmail_thenReturnsCartResponseDto() {
        //arrange
        List<CartItem> items = new ArrayList<>();
        items.add(testCartItem);
        testCart.setItems(items);
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUserWithItems(testUser)).thenReturn(Optional.of(testCart));
        
        CartResponseDto result = cartService.getByUserEmail(testUser.getEmail());
        
        //assert
        assertNotNull(result);
        assertEquals(testCart.getId(), result.id());
        verify(cartRepo).findByUserWithItems(testUser);
    }

    @Test
    void givenNonExistingUserEmail_whenGetByUserEmail_thenThrowsResourceNotFoundException() {
        //arrange
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUserWithItems(testUser)).thenReturn(Optional.empty());
        
        //assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.getByUserEmail(testUser.getEmail()));
    }

    @Test
    void givenValidDtoAndEmail_whenAddItem_thenReturnsCartResponseDto() {
        //arrange
        CartItemRequestDto request = new CartItemRequestDto(1L, 2L);
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.of(testCart));
        
        CartResponseDto result = cartService.addItem(request, testUser.getEmail());
        
        //assert
        assertNotNull(result);
        verify(cartItemService).addItem(testCart, request);
    }

    @Test
    void givenNonExistingCartForUser_whenAddItem_thenThrowsResourceNotFoundException() {
        //arrange
        CartItemRequestDto request = new CartItemRequestDto(1L, 2L);
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.empty());
        
        //assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.addItem(request, testUser.getEmail()));
    }

    @Test
    void givenValidCartItemIdAndEmail_whenDeleteItem_thenItemIsDeletedSuccessfully() {
        //arrange
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        
        cartService.deleteItem(testCartItem.getId(), testUser.getEmail());
        
        //assert
        verify(cartItemService).deleteItem(testCartItem.getId());
    }

    @Test
    void givenValidItemIdAndDto_whenUpdateItem_thenReturnsCartItemResponseDto() {
        // arrange
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto(3L);
        testCart.setItems(List.of(testCartItem));
        CartItemResponseDto expectedResponse = CartItemResponseDto.fromEntity(testCartItem);

        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartItemService.updateQuantityByCartItemId(testCartItem.getId(), request.quantity()))
                .thenReturn(expectedResponse);

        // act
        CartItemResponseDto result = cartService.updateItem(testCartItem.getId(), request, testUser.getEmail());

        // assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(cartItemService).updateQuantityByCartItemId(testCartItem.getId(), request.quantity());
    }

    @Test
    void givenNonExistingCartForUser_whenUpdateItem_thenThrowsResourceNotFoundException() {
        //arrange
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto(3L);
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.empty());
        
        //assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.updateItem(testCartItem.getId(), request, testUser.getEmail()));
    }

    @Test
    void givenNonExistingCartItemId_whenUpdateItem_thenThrowsResourceNotFoundException() {
        //arrange
        UpdateCartItemRequestDto request = new UpdateCartItemRequestDto(3L);
        List<CartItem> items = new ArrayList<>();
        testCart.setItems(items);
        
        //act
        when(userService.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.of(testCart));
        
        //assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.updateItem(testCartItem.getId(), request, testUser.getEmail()));
    }

    @Test
    void givenValidUser_whenGetEntityByUser_thenReturnsCartEntity() {
        //arrange
        
        //act
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.of(testCart));
        
        Cart result = cartService.getEntityByUser(testUser);
        
        //assert
        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        verify(cartRepo).findByUser(testUser);
    }

    @Test
    void givenNonExistingUser_whenGetEntityByUser_thenThrowsResourceNotFoundException() {
        //arrange
        
        //act
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.empty());
        
        //assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.getEntityByUser(testUser));
    }

    @Test
    void givenValidUser_whenCleanByUser_thenCartItemsAreClearedSuccessfully() {
        //arrange
        List<CartItem> items = new ArrayList<>();
        items.add(testCartItem);
        testCart.setItems(items);
        
        //act
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.of(testCart));
        
        cartService.cleanByUser(testUser);
        
        //assert
        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepo).save(testCart);
    }

    @Test
    void givenNonExistingUser_whenCleanByUser_thenThrowsResourceNotFoundException() {
        //arrange
        
        //act
        when(cartRepo.findByUser(testUser)).thenReturn(Optional.empty());
        
        //assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.cleanByUser(testUser));
        verify(cartRepo, never()).save(any(Cart.class));
    }
}
