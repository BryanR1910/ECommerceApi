package com.bryan.ECommerceApi.repository;

import com.bryan.ECommerceApi.model.Order;
import com.bryan.ECommerceApi.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    void deleteByOrder(Order order);
}
