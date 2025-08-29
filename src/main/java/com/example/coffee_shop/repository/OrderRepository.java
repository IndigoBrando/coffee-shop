package com.example.coffee_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.coffee_shop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // later you can add custom queries like findByUserId, findByStatus, etc.
}