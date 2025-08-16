package com.example.coffee_shop.controller;

import com.example.coffee_shop.model.User;
import com.example.coffee_shop.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("errorMessage", "Email already registered!");
            return "register";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("errorMessage", "Passwords do not match!");
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setConfirmPassword(null);
        userRepository.save(user);
        return "redirect:/login";
    }

    // Login GET
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // Login POST
    @PostMapping("/login")
public String login(@ModelAttribute("user") User user, Model model, HttpSession session) {
    User existingUser = userRepository.findByEmail(user.getEmail());
    if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
        model.addAttribute("errorMessage", "Invalid email or password");
        return "login";
    }

    // Store user in session
    session.setAttribute("loggedUser", existingUser);

    // Redirect based on role
    if (existingUser.getRole() == 1) {
        return "redirect:/admin/dashboard";
    } else if (existingUser.getRole() == 2) {
        return "redirect:/user/home";
    }
    return "redirect:/login";
}

    //Normal users
     @GetMapping("/user/home")
    public String userHome(HttpSession session) {
         User loggedUser = (User) session.getAttribute("loggedUser");
         if(loggedUser == null){
            return "redirect:/login";
         }

         if(loggedUser.getRole() != 2){
            return "redirect:/access-denied";
         }
        return "user/home"; 
    }

  

    @GetMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate(); // remove all session data
    return "redirect:/login";
}

    
}
