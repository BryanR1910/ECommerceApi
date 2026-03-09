package com.bryan.ECommerceApi.repository;

import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

}
