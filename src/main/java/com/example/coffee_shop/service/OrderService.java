package com.example.coffee_shop.service;

import com.example.coffee_shop.model.CartItem;
import com.example.coffee_shop.model.Order;
import com.example.coffee_shop.model.OrderItem;
import com.example.coffee_shop.repository.CartItemRepository;
import com.example.coffee_shop.repository.OrderItemRepository;
import com.example.coffee_shop.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Order checkout(Long userId) {
        // 1. Get all cart items
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // 2. Calculate total
        BigDecimal total = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Create new order
        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setOrderDate(LocalDateTime.now());
        order = orderRepository.save(order);

        // 4. Save each cart item as an order item
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCoffee(cartItem.getCoffee());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            orderItemRepository.save(orderItem);
        }

        // 5. Clear cart
        cartItemRepository.deleteAll(cartItems);

        return order;
    }
}
