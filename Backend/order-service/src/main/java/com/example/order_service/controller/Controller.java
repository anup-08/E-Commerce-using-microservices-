package com.example.order_service.controller;

import com.example.order_service.dto.OrderResponse;
import com.example.order_service.dto.UpdateRequest;
import com.example.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class Controller {

    private final OrderService orderService;

    @PostMapping("/cart/orders")
    public ResponseEntity<OrderResponse> postOrder(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(orderService.placeOrder(jwt.getSubject()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getByOrderId(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/update/status")
    public ResponseEntity<OrderResponse> updateOrder(@Valid @RequestBody UpdateRequest request){
        return ResponseEntity.ok(orderService.updateOrderStatus(request.getOrderId(), request.getOrderStatus()));
    }
}
