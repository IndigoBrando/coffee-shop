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
        return "product-add";
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
        return "redirect:/admin/products";
    }

    // Edit coffee
   @PostMapping("/products/edit/{id}")
public String updateProduct(@PathVariable("id") Long id, Coffee updatedCoffee, HttpSession session) {
    if (!isAdmin(session)) {
        return "redirect:/access-denied";
    }

    updatedCoffee.setId(id); // ensure same ID
    coffeeRepository.save(updatedCoffee);
    return "redirect:/admin/products"; // ✅ back to product list
}

  
  @PostMapping("/update")
    public String updateCoffee(@ModelAttribute("coffee") Coffee coffee,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Coffee existingCoffee = coffeeRepository.findById(coffee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid coffee ID: " + coffee.getId()));

        existingCoffee.setName(coffee.getName());
        existingCoffee.setDescription(coffee.getDescription());
        existingCoffee.setPrice(coffee.getPrice());
        existingCoffee.setStock(coffee.getStock());

        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                String fileName = coffee.getId() + ".png"; // save by ID
                Path filePath = Paths.get(uploadDir, fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                existingCoffee.setImage("/uploads/" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        coffeeRepository.save(existingCoffee);
        return "redirect:/admin/products"; // ✅ go back to product list
    }

    // Delete coffee
   @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/access-denied";
        }
        coffeeRepository.deleteById(id);
        return "redirect:/admin/products";
    }

}
