package com.cartService.service;

import com.cartService.dto.KafkaDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventListener {

    private final CartService service;

    @KafkaListener(topics = "clear-cart-topic", groupId = "cart-service-group")
    public void handleKafkaListener(KafkaDto event){
        System.out.println("Received request to clear cart for user: " + event.getUserId());
        try{
            service.clearCart(event.getUserId());
        } catch (FeignException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
