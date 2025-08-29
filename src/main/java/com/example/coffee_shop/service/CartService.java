package com.example.coffee_shop.service;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

import com.example.coffee_shop.model.CartItem;
import com.example.coffee_shop.model.Coffee;
import com.example.coffee_shop.repository.CartItemRepository;
import com.example.coffee_shop.repository.CoffeeRepository;
@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void addToCart(Long userId, Long coffeeId, int quantity) {
        Coffee coffee = coffeeRepository.findById(coffeeId)
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        BigDecimal subtotal = coffee.getPrice().multiply(BigDecimal.valueOf(quantity));

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setCoffee(coffee);
        cartItem.setQuantity(quantity);
        cartItem.setSubtotal(subtotal);

        cartItemRepository.save(cartItem);
    }

    public void updateCartItem(Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);
        item.setSubtotal(item.getCoffee().getPrice().multiply(BigDecimal.valueOf(quantity)));

        cartItemRepository.save(item);
    }

    public void removeCartItem(Long itemId) {
        cartItemRepository.deleteById(itemId);
    }

    public BigDecimal calculateTotal(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
