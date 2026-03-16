package com.bryan.EcommerceAPi.service;

import com.bryan.ECommerceApi.model.*;
import com.bryan.ECommerceApi.repository.OrderItemRepo;
import com.bryan.ECommerceApi.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {
    @Mock
    private OrderItemRepo orderItemRepo;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void givenCartAndOrder_whenAddItems_thenSaveOrderItemsCorrectly(){
        //arrange
        Product product = new Product(1L, "Product", "Desc", BigDecimal.valueOf(100), 10L, "img.jpg", "Category");

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1L);

        Cart cart = new Cart();
        cart.setItems(List.of(cartItem));

        Order order = new Order();

        //act
        orderItemService.addItems(cart,order);

        //assert
        ArgumentCaptor<List<OrderItem>> orderItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepo).saveAll(orderItemsCaptor.capture());

        List<OrderItem> items = orderItemsCaptor.getValue();
        assertEquals(1, items.size());
        assertEquals(1, items.getFirst().getQuantity());
        assertEquals(BigDecimal.valueOf(100), items.getFirst().getPriceAtPurchase());
        assertEquals(order, items.getFirst().getOrder());
        assertEquals(product, items.getFirst().getProduct());
    }

    @Test
    void givenOrderWithItems_whenDeleteByOrder_thenClearsOrderItems() {
        // Arrange
        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem());
        items.add(new OrderItem());

        Order order = new Order();
        order.setItems(items);

        // Act
        orderItemService.deleteByOrder(order);

        // Assert
        assertTrue(order.getItems().isEmpty());
    }
}
