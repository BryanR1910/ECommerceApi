package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.dto.CartItemResponseDto;
import com.bryan.ECommerceApi.model.dto.CartResponseDto;
import com.bryan.ECommerceApi.model.dto.CartItemRequestDto;
import com.bryan.ECommerceApi.model.dto.UpdateCartItemRequestDto;
import com.bryan.ECommerceApi.repository.CartRepo;
import org.springframework.stereotype.Service;


@Service
public class CartService {
    private final CartRepo cartRepo;
    private final UserService userService;
    private final CartItemService cartItemService;

    public CartService(CartRepo cartRepo, UserService userService, CartItemService cartItemService) {
        this.cartRepo = cartRepo;
        this.userService = userService;
        this.cartItemService = cartItemService;
    }

    public void create(User user){
        Cart cart = new Cart(user);
        cartRepo.save(cart);
    }

    public CartResponseDto getByUserEmail(String email){
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUserWithItems(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "User", email));
        return CartResponseDto.fromEntity(cart);
    }

    public CartResponseDto addItem(CartItemRequestDto dto, String email){
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", user.getEmail()));
        cartItemService.addItem(cart, dto);

        return CartResponseDto.fromEntity(cart);
    }

    public void deleteItem(Long cartItemId, String email){
        User user = userService.findByEmail(email);
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", email));

        cartItemService.deleteItem(cartItemId, cart);
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

    public Cart getEntityByUser(User user){
        return cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", user.getEmail()));
    }

    public void cleanByUser(User user){
        Cart cart = cartRepo.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart", "User", user.getEmail()));
        cart.getItems().clear();
        cartRepo.save(cart);
    }

}
