package com.cartService.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class CartItemResponseDto {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}
