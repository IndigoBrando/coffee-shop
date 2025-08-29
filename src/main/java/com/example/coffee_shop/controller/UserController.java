package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.repository.CoffeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class UserController {

    private final CoffeeRepository coffeeRepository;

    public UserController(CoffeeRepository coffeeRepository) { // ✅ match class name
        this.coffeeRepository = coffeeRepository;
    }

    // Show all available coffees
    @GetMapping
    public String showMenu(Model model) {
        List<Coffee> coffees = coffeeRepository.findAll();
        model.addAttribute("coffees", coffees);
        return "user/menu"; 
    }
}
