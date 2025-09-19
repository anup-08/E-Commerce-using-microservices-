package com.cartService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
    @NotBlank(message = "Product ID cannot be empty.")
    private String productId;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity = 1;
}
