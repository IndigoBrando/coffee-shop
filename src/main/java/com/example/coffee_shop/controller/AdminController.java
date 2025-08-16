package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.User;
import com.example.coffee_shop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
      
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

     
        if (loggedUser.getRole() != 1) {
            return "redirect:/access-denied";
        }

        long totalUsers = userRepository.count();
        model.addAttribute("totalUsers", totalUsers);

        return "admin/dashboard";
    }
}
