package com.example.order_service.feignClient;

import com.example.order_service.dto.InventoryRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "Inventory-service")
public interface InventoryFeignClient {

    @PostMapping("/inventory/reservations")
    public void reserveStock(@Valid @RequestBody List<InventoryRequest> request);


}
