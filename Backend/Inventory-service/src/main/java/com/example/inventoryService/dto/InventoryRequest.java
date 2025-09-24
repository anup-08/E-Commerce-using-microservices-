package com.example.inventoryService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    @NotBlank(message = "Product ID cannot be null or empty.")
    private String productId;

    @NotBlank(message = "SKU cannot be null or empty.")
    private String sku;

    @NotNull(message = "Available quantity cannot be null.")
    @Min(value = 0, message = "Available quantity cannot be negative.")
    private Integer quantity ;
}
