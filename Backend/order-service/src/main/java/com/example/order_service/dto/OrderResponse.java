package com.example.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String userId;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime orderAt;
    private List<OrderItemResponse> orderItems;
}
