package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public OrderResponse placeOrder(@PathVariable Long userId) {
        return orderService.placeOrder(userId);
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> myOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping
    public List<OrderResponse> allOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}
