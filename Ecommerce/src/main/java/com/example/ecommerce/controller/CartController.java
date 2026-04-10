package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CartRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public List<Cart> getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}")
    public Cart addToCart(@PathVariable Long userId, @Valid @RequestBody CartRequest request) {
        return cartService.addToCart(userId, request);
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared");
    }
}
