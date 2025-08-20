package com.example.coffee_shop.controller;
import com.example.coffee_shop.repository.CoffeeRepository; 
import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.model.User;
import com.example.coffee_shop.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;


    private boolean isAdmin(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && loggedUser.getRole() == 1;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }

        long totalUsers = userRepository.count();
        model.addAttribute("totalUsers", totalUsers);

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String showProductsPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("products", coffeeRepository.findAll()); 
        return "admin/products"; 
    }

    @GetMapping("/products-add")
    public String showAddProductPage(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("coffee", new Coffee());
        return "admin/products-add";
    }
 @GetMapping("/products/edit/{id}")
public String showEditProductPage(@PathVariable("id") Long id, HttpSession session, Model model) {
    if (!isAdmin(session)) {
        return "redirect:/access-denied";
    }

    Coffee coffee = coffeeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

    model.addAttribute("coffee", coffee);
    return "admin/products-edit";  // ✅ make sure you create products-edit.html
}

  

  
}
