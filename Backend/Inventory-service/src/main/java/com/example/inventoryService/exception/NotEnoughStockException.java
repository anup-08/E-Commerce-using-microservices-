package com.example.inventoryService.exception;

import jakarta.validation.constraints.NotBlank;

public class NotEnoughStockException extends Throwable {
    public NotEnoughStockException(@NotBlank(message = "SKU cannot be null or empty.") String s) {
    }
}
