package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.exception.InsufficientStockException;
import com.bryan.ECommerceApi.exception.ResourceNotFoundException;
import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.CartItem;
import com.bryan.ECommerceApi.model.Product;
import com.bryan.ECommerceApi.model.dto.CartItemResponseDto;
import com.bryan.ECommerceApi.model.dto.CartResponseDto;
import com.bryan.ECommerceApi.model.dto.ItemRequestDto;
import com.bryan.ECommerceApi.repository.CartItemRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemService {
    private final CartItemRepo cartItemRepo;
    private final ProductService productService;

    public CartItemService(CartItemRepo cartItemRepo, ProductService productService) {
        this.cartItemRepo = cartItemRepo;
        this.productService = productService;
    }

    public void addItem(Cart cart, ItemRequestDto dto){
        Product product = productService.findEntityById(dto.productId());

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter((i) -> i.getProduct().getId().equals(dto.productId()))
                .findFirst();

        if(existingItem.isPresent()){
            Long newQuantity = existingItem.get().getQuantity() + dto.quantity();
            if(newQuantity > product.getStock()){
                throw new InsufficientStockException(product.getName());
            }
            existingItem.get().setQuantity(newQuantity);
            cartItemRepo.save(existingItem.get());
            return;
        }

        if(dto.quantity() > product.getStock()){
            throw new InsufficientStockException(product.getName());
        }

        CartItem item = new CartItem(cart, product, dto.quantity());
        cartItemRepo.save(item);
        cart.getItems().add(item);
    }

    public void deleteItem(Long cartItemId){
        CartItem item = cartItemRepo.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        cartItemRepo.delete(item);
    }

    public CartItemResponseDto updateQuantityByCartItemId(Long itemId, Long quantity) {
        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (quantity > item.getProduct().getStock()) {
            throw new InsufficientStockException(item.getProduct().getName());
        }

        item.setQuantity(quantity);
        return CartItemResponseDto.fromEntity(cartItemRepo.save(item));
    }



}
