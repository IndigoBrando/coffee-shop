package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.CartItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.coffee_shop.service.CartService;
import com.example.coffee_shop.service.OrderService;

import jakarta.servlet.http.HttpSession;
import com.example.coffee_shop.model.User;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    // View cart
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Long userId = loggedUser.getId();
        List<CartItem> cartItems = cartService.getCartItems(userId);
        BigDecimal total = cartService.calculateTotal(userId);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "user/cart";
    }

    // Add to cart
    @PostMapping("/add")
    public String addToCart(@RequestParam Long coffeeId, @RequestParam int quantity, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Long userId = loggedUser.getId();
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

    // Checkout
    @PostMapping("/checkout")
    public String checkout(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Long userId = loggedUser.getId();
        orderService.checkout(userId);
    return "redirect:/menu/order"; // now goes to /menu/order
    }
}
