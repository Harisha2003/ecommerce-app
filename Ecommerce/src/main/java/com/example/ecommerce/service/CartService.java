package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CartRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<Cart> getCart(Long userId) {
        log.info("Fetching cart for userId: {}", userId);
        List<Cart> items = cartRepository.findByUserId(userId);
        log.info("Cart items found: {}", items.size());
        return items;
    }

    public Cart addToCart(Long userId, CartRequest request) {
        log.info("Adding to cart - userId: {}, productId: {}, quantity: {}", userId, request.productId(), request.quantity());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", request.productId());
                    return new ResourceNotFoundException("Product not found");
                });

        Cart cart = cartRepository.findByUserIdAndProductId(userId, product.getId())
                .orElse(new Cart());
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(cart.getQuantity() == null ? request.quantity() : cart.getQuantity() + request.quantity());
        Cart saved = cartRepository.save(cart);
        log.info("Cart updated - cartId: {}, product: {}, quantity: {}", saved.getId(), product.getName(), saved.getQuantity());
        return saved;
    }

    public void removeFromCart(Long cartItemId) {
        log.info("Removing cart item with id: {}", cartItemId);
        cartRepository.deleteById(cartItemId);
        log.info("Cart item removed with id: {}", cartItemId);
    }

    public void clearCart(Long userId) {
        log.info("Clearing cart for userId: {}", userId);
        cartRepository.deleteByUserId(userId);
        log.info("Cart cleared for userId: {}", userId);
    }
}
