package com.example.order_service.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pending Payment"),
    CONFIRMED("Order Confirmed"),
    PAYMENT_FAILED("Payment Failed"),
    DELIVERED("Order Delivered"),
    PROCESSING("Order Processing"),
    CANCELED("Order Cancelled");

    private final String displayName;

    OrderStatus(String displayName){
        this.displayName = displayName;
    }

}
