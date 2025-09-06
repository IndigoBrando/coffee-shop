package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.CartItem;
import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.model.Order;
import com.example.coffee_shop.model.OrderItem;
import com.example.coffee_shop.repository.CartItemRepository;
import com.example.coffee_shop.repository.CoffeeRepository;
import com.example.coffee_shop.repository.OrderRepository;
import com.example.coffee_shop.repository.OrderItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ✅ Place a single order directly (from menu)
    @PostMapping("/add")
    public String addOrder(@RequestParam Long coffeeId,
                           @RequestParam int quantity,
                           @RequestParam Long userId) {

        Coffee coffee = coffeeRepository.findById(coffeeId)
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        if (coffee.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available!");
        }

        
        Order order = new Order();
        order.setUserId(userId); 
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        orderRepository.save(order);

      
        BigDecimal price = coffee.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setCoffee(coffee);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setSubtotal(subtotal);
        orderItemRepository.save(orderItem);

       
        order.setTotalAmount(subtotal); 
        orderRepository.save(order);

  
        coffee.setStock(coffee.getStock() - quantity);
        coffeeRepository.save(coffee);

        return "redirect:/menu";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        // create new order
        Order order = new Order();
        order.setUserId(userId); 
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalAmount(BigDecimal.ZERO);
        orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Coffee coffee = cartItem.getCoffee();
            int qty = cartItem.getQuantity();
            BigDecimal subtotal = coffee.getPrice().multiply(BigDecimal.valueOf(qty));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCoffee(coffee);
            orderItem.setQuantity(qty);
            orderItem.setPrice(coffee.getPrice());
            orderItem.setSubtotal(subtotal);
            orderItemRepository.save(orderItem);

            total = total.add(subtotal);

            coffee.setStock(coffee.getStock() - qty);
            coffeeRepository.save(coffee);
        }

        order.setTotalAmount(total); 
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return "redirect:/menu/order-history?userId=" + userId;
    }
}
