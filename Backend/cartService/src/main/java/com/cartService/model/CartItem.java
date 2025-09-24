package com.cartService.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @NotBlank(message = "Product ID cannot be empty.")
    private String productId;

    @Builder.Default
    private Integer quantity = 1;

    @NotNull(message = "Unit price cannot be null.")
    @Positive(message = "Unit price must be positive.")
    private BigDecimal price;

    private String productName;
    @NotBlank(message = "sku cannot be empty.")
    private String sku;
}
