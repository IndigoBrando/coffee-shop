package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.model.Order;
import com.example.coffee_shop.repository.CoffeeRepository;
import com.example.coffee_shop.repository.OrderRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.coffee_shop.model.User;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/menu")
public class UserController {

    private final CoffeeRepository coffeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    public UserController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    // ✅ Show all available coffees
    @GetMapping
    public String showMenu(Model model) {
        List<Coffee> coffees = coffeeRepository.findAll();
        model.addAttribute("coffees", coffees);
        return "user/menu";
    }

    // ✅ Order history
   @GetMapping("/order") // or "/order/history" if you prefer
public String orderHistory(HttpSession session, Model model) {
    User loggedUser = (User) session.getAttribute("loggedUser");

    if (loggedUser == null) {
        return "redirect:/login";
    }

    List<Order> orders = orderRepository.findByUserId(loggedUser.getId());
    model.addAttribute("orders", orders);

    return "user/order"; // path to templates/user/order.html
}


}
