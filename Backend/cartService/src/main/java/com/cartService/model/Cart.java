package com.cartService.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    private String cartId;

    @NotBlank(message = "User ID cannot be empty.")
    private String userId;

    @Valid
    private List<CartItem> cartItems;

    @NotNull(message = "Total price cannot be null.")
    @PositiveOrZero(message = "Total price must be zero or greater.")
    private BigDecimal totalPrice;

}
