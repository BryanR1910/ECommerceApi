package com.bryan.ECommerceApi.service;

import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.CartItem;
import com.bryan.ECommerceApi.model.Order;
import com.bryan.ECommerceApi.model.OrderItem;
import com.bryan.ECommerceApi.repository.OrderItemRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepo orderItemRepo;

    public OrderItemService(OrderItemRepo orderItemRepo) {
        this.orderItemRepo = orderItemRepo;
    }

    public void addItems(Cart cart, Order order){
        List<OrderItem> items = new ArrayList<>();

        for(CartItem cartItem : cart.getItems()){
            OrderItem orderItem = new OrderItem(
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice(),
                    order,
                    cartItem.getProduct()
            );
            items.add(orderItem);
        }

        orderItemRepo.saveAll(items);
    }

    public void deleteByOrder(Order order){
        order.getItems().clear();
    }
}
