package com.example.ecommerce.dto.response;

import com.example.ecommerce.entity.Order.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String username,
        List<OrderItemResponse> items,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
    public record OrderItemResponse(String productName, Integer quantity, BigDecimal price) {}
}
