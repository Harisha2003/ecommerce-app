package com.example.ecommerce.service;

import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.dto.response.OrderResponse.OrderItemResponse;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse placeOrder(Long userId) {
        log.info("Placing order for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        List<Cart> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            log.warn("Cart is empty for userId: {}", userId);
            throw new IllegalStateException("Cart is empty");
        }

        log.info("Cart has {} item(s) for userId: {}", cartItems.size(), userId);

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = cartItems.stream().map(cart -> {
            Product product = cart.getProduct();
            log.info("Processing product: {}, stock: {}, requested: {}", product.getName(), product.getStock(), cart.getQuantity());
            if (product.getStock() < cart.getQuantity()) {
                log.error("Insufficient stock for product: {}", product.getName());
                throw new IllegalStateException("Insufficient stock for: " + product.getName());
            }
            product.setStock(product.getStock() - cart.getQuantity());
            productRepository.save(product);
            log.info("Stock updated for product: {}, remaining stock: {}", product.getName(), product.getStock());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(cart.getQuantity());
            item.setPrice(product.getPrice());
            return item;
        }).toList();

        order.setItems(orderItems);
        order.setTotalAmount(orderItems.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        orderRepository.save(order);
        cartRepository.deleteByUserId(userId);

        log.info("Order placed successfully - orderId: {}, totalAmount: {}", order.getId(), order.getTotalAmount());
        return toResponse(order);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        log.info("Fetching orders for userId: {}", userId);
        List<OrderResponse> orders = orderRepository.findByUserId(userId).stream().map(this::toResponse).toList();
        log.info("Total orders found for userId {}: {}", userId, orders.size());
        return orders;
    }

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        List<OrderResponse> orders = orderRepository.findAll().stream().map(this::toResponse).toList();
        log.info("Total orders found: {}", orders.size());
        return orders;
    }

    public OrderResponse updateStatus(Long orderId, Order.OrderStatus status) {
        log.info("Updating status for orderId: {} to {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found with id: {}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        log.info("Order status updated - orderId: {}, status: {}", saved.getId(), saved.getStatus());
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProduct().getName(), i.getQuantity(), i.getPrice()))
                .toList();
        return new OrderResponse(order.getId(), order.getUser().getUsername(),
                items, order.getStatus(), order.getTotalAmount(), order.getCreatedAt());
    }
}
