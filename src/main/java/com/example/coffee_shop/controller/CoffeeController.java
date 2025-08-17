package com.example.coffee_shop.controller;
import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.model.User;
import com.example.coffee_shop.repository.CoffeeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;              
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
@RequestMapping("/products")
public class CoffeeController {

    private final CoffeeRepository coffeeRepository;

    public CoffeeController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    
    private boolean isAdmin(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        return loggedUser != null && loggedUser.getRole() == 1;
    }

    // List all coffee products (Admin only)
 @GetMapping
public String listProducts(HttpSession session, Model model) {
    if (!isAdmin(session)) {
        return "redirect:/access-denied";
    }
    model.addAttribute("products", coffeeRepository.findAll());
    return "products"; 
}

   
    @GetMapping("/add")
    public String showAddForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }
        model.addAttribute("coffee", new Coffee());
        return "product-form"; 
    }

  
  @PostMapping("/save")
public String saveCoffee(@ModelAttribute("coffee") Coffee coffee,
                         @RequestParam("imageFile") MultipartFile imageFile) {
    try {
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String fileName = imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            coffee.setImage("/uploads/" + fileName);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    coffeeRepository.save(coffee);
    return "redirect:/admin/products";  // ✅ match your list mapping
}

    // Edit coffee
    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }
        Coffee coffee = coffeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coffee Id:" + id));
        model.addAttribute("coffee", coffee);
        return "product-form";
    }

    // Delete coffee
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }
        coffeeRepository.deleteById(id);
        return "redirect:/products";
    }
}
