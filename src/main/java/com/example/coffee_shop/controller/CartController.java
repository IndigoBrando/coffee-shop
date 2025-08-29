package com.example.coffee_shop.controller;
import com.example.coffee_shop.model.CartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.coffee_shop.service.CartService;


import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // View cart
    @GetMapping
    public String viewCart(Model model, Principal principal) {
        Long userId = 1L; // replace later with real logged-in user
        List<CartItem> cartItems = cartService.getCartItems(userId);
        BigDecimal total = cartService.calculateTotal(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "user/cart";

    }

    // Add to cart
    @PostMapping("/add")
    public String addToCart(@RequestParam Long coffeeId, @RequestParam int quantity, Principal principal) {
        Long userId = 1L;
        cartService.addToCart(userId, coffeeId, quantity);
        return "redirect:/cart";
    }

    // Remove from cart
    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return "redirect:/cart";
    }

    // Update quantity
    @PostMapping("/update/{id}")
    public String updateCartItem(@PathVariable Long id, @RequestParam int quantity) {
        cartService.updateCartItem(id, quantity);
        return "redirect:/cart";
    }
}
