package com.example.order_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CartItemResponseDto {
    private String productId;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}
