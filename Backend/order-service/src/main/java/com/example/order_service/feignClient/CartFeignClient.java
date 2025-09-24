package com.example.order_service.feignClient;

import com.example.order_service.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cartService")
public interface CartFeignClient {

    @GetMapping("/cart/get/details")
    public CartResponse getDetailsOfCart();

}
