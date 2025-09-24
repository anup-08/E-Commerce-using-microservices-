package com.example.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private String productId;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
}
