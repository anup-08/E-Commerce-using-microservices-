package com.example.inventoryService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long inventoryId;
    private String productId;
    private String sku;
    private Integer totalQuantity;
    private Integer reservedQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
