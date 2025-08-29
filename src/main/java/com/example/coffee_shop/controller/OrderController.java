package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.model.Order;
import com.example.coffee_shop.model.OrderItem;
import com.example.coffee_shop.repository.CoffeeRepository;
import com.example.coffee_shop.repository.OrderRepository;
import com.example.coffee_shop.repository.OrderItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PostMapping("/add")
    public String addOrder(@RequestParam Long coffeeId,
            @RequestParam int quantity,
            @RequestParam Long userId) {

        Coffee coffee = coffeeRepository.findById(coffeeId)
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        if (coffee.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available!");
        }

        // create new order
        Order order = new Order();
        order.setUser_id(userId); // ✅ set user_id
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        orderRepository.save(order);

        // create order item
        BigDecimal price = coffee.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setCoffee(coffee);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setSubtotal(subtotal);
        orderItemRepository.save(orderItem);

        // update order total
        order.setTotal_amount(subtotal);
        orderRepository.save(order);

        // reduce stock
        coffee.setStock(coffee.getStock() - quantity);
        coffeeRepository.save(coffee);

        return "redirect:/menu";
    }
}
