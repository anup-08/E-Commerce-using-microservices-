package com.example.inventoryService.controller;

import com.example.inventoryService.dto.InventoryRequest;
import com.example.inventoryService.dto.InventoryResponse;
import com.example.inventoryService.exception.NotEnoughStockException;
import com.example.inventoryService.service.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/inventory")
public class Controller {

    private final InventoryService service;

    @PostMapping("/reservations")
    public ResponseEntity<String> reserveStock(@Valid @RequestBody List<InventoryRequest> request) throws NotEnoughStockException {
        service.reserveStock(request);
        return ResponseEntity.ok("Stock reserve Successfully");
    }

    @GetMapping("/availability/{sku}")
    public ResponseEntity<Boolean> isAvailable(@PathVariable String sku ,
                                               @RequestParam(defaultValue = "1") long quantity){
        return  ResponseEntity.ok(service.isAvailable(sku,quantity));
    }

    @PostMapping("/reservations/cancellation")
    public ResponseEntity<String> releaseStock(@Valid @RequestBody List<InventoryRequest> request){
        service.releaseStock(request);
        return ResponseEntity.ok("Stock Release successfully");
    }

    @PostMapping("/fulfillment")
    public ResponseEntity<String> fulfillStock(@Valid @RequestBody List<InventoryRequest> request){
        service.fulfillStock(request);
        return ResponseEntity.ok("Stock fulfillStock successfully");
    }

    @PostMapping("/add/stock")
    public ResponseEntity<List<InventoryResponse>> addStock(@Valid @RequestBody List<InventoryRequest> requests){
        return new ResponseEntity<>(service.addStock(requests) , HttpStatus.CREATED);
    }
}
