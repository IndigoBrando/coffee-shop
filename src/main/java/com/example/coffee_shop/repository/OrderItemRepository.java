package com.example.coffee_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.coffee_shop.model.OrderItem;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // e.g. find items by order id
    List<OrderItem> findByOrderId(Long orderId);
}