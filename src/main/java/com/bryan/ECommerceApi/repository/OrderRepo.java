package com.bryan.ECommerceApi.repository;

import com.bryan.ECommerceApi.model.Order;
import com.bryan.ECommerceApi.model.User;
import com.bryan.ECommerceApi.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {
    Optional<Order> findByUserAndStatus(User user, Status status);

    Optional<Order> findByStripePaymentId(String paymentIntentId);
}
