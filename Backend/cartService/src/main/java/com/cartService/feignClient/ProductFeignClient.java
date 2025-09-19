package com.cartService.feignClient;

import com.cartService.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productService" )
public interface ProductFeignClient {

    @GetMapping("product/get/{productId}")
    public ProductResponse getProduct(@PathVariable String productId);

}
