package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.CartItem;
import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.dto.CartItemResponseDto;
import com.bryan.ECommerceApi.model.dto.CartResponseDto;
import com.bryan.ECommerceApi.model.dto.ItemRequestDto;
import com.bryan.ECommerceApi.model.dto.UpdateCartItemRequestDto;
import com.bryan.ECommerceApi.repository.CartRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepo cartRepo;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final EntityManager entityManager;

    public CartService(CartRepo cartRepo, UserService userService, CartItemService cartItemService, EntityManager entityManager) {
        this.cartRepo = cartRepo;
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.entityManager = entityManager;
    }

    public void create(User user){
        Cart cart = new Cart(user);
        cartRepo.save(cart);
    }

    public CartResponseDto getByUserEmail(String email){
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUserWithItems(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", email));
        return CartResponseDto.fromEntity(cart);
    }

    public CartResponseDto addItem(ItemRequestDto dto, String email){
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", user.getEmail()));
        cartItemService.addItem(cart, dto);

        return CartResponseDto.fromEntity(cart);
    }

    public void deleteItem(Long cartItemId, String email){
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", email));

        cart.getItems().stream()
                .filter((item) -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cartItemService.deleteItem(cartItemId);
    }


    public CartItemResponseDto updateItem(Long itemId, UpdateCartItemRequestDto dto, String email) {
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", email));

        cart.getItems().stream()
                .filter((item) -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        return cartItemService.updateQuantityByCartItemId(itemId, dto.quantity());
    }
}
