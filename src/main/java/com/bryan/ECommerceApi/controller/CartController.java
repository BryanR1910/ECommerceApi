package com.bryan.ECommerceApi.controller;

import com.bryan.ECommerceApi.model.dto.CartItemResponseDto;
import com.bryan.ECommerceApi.model.dto.CartResponseDto;
import com.bryan.ECommerceApi.model.dto.CartItemRequestDto;
import com.bryan.ECommerceApi.model.dto.UpdateCartItemRequestDto;
import com.bryan.ECommerceApi.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getByUser(Authentication authentication){
        String email = authentication.getName();
        CartResponseDto response = cartService.getByUserEmail(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(@Valid @RequestBody CartItemRequestDto dto, Authentication authentication){
        String email = authentication.getName();
        CartResponseDto response = cartService.addItem(dto, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id, Authentication authentication){
        String email = authentication.getName();
        cartService.deleteItem(id, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemResponseDto> updateItem(@PathVariable("id") Long itemId,
                                                          @RequestBody UpdateCartItemRequestDto dto,
                                                          Authentication authentication
    ){
        String email = authentication.getName();
        CartItemResponseDto response = cartService.updateItem(itemId, dto, email);
        return ResponseEntity.ok(response);
    }

}
