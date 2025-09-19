package com.cartService.controller;

import com.cartService.dto.AddItemRequest;
import com.cartService.dto.CartResponse;
import com.cartService.dto.UpdateRequest;
import com.cartService.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/cart")
public class Controller {

    private final CartService service;

    @PostMapping("/add/item")
    public ResponseEntity<CartResponse> addToCart(@AuthenticationPrincipal Jwt jwt ,
                                                  @Valid @RequestBody AddItemRequest request){
        String userId = jwt.getSubject();
        return new ResponseEntity<>(service.addToCart(request , userId), HttpStatus.CREATED);
    }

    @PutMapping("/update/item")
    public ResponseEntity<CartResponse> updateItemQuantity(@AuthenticationPrincipal Jwt jwt ,
                                                           @Valid @RequestBody UpdateRequest request){
        return ResponseEntity.ok(service.updateItemQuantity(request , jwt.getSubject()));
    }

    @DeleteMapping("/delete/Cart/item/{productId}")
    public ResponseEntity<CartResponse> deleteCartItem(@AuthenticationPrincipal Jwt jwt,
                                                        @PathVariable String productId){
        return ResponseEntity.ok(service.deleteCartItem(productId , jwt.getSubject()));
    }

    @GetMapping("/get/details")
    public ResponseEntity<CartResponse> getDetailsOfCart(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(service.getDetailsOfCart(jwt.getSubject()));
    }

    @DeleteMapping("/clear/cart")
    public ResponseEntity<CartResponse> clearCart(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(service.clearCart(jwt.getSubject()));
    }
}
