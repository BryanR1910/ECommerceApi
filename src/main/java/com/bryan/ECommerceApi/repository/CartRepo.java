package com.bryan.ECommerceApi.repository;

import com.bryan.ECommerceApi.model.Cart;
import com.bryan.ECommerceApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepo extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    @Query("SELECT c FROM Cart c JOIN FETCH c.items i JOIN FETCH i.product WHERE c.user = :user")
    Optional<Cart> findByUserWithItems(@Param("user") User user);
}
