package com.example.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRequest {
    @NotNull(message = "order Id cannot be blank")
    private Long orderId;
    @NotBlank(message = "order Status is required")
    private String orderStatus;
}
