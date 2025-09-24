package com.example.inventoryService.service;

import com.example.inventoryService.dto.InventoryRequest;
import com.example.inventoryService.dto.InventoryResponse;
import com.example.inventoryService.exception.NoProductFound;
import com.example.inventoryService.exception.NotEnoughStockException;
import com.example.inventoryService.model.Inventory;
import com.example.inventoryService.repo.InventoryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class InventoryService {

    private final InventoryRepo repo;

    public boolean isAvailable(String sku , Long quantity){
        Inventory inventory = repo.findBySku(sku).orElseThrow(() -> new NoProductFound("No Product Found with sku "+sku));
        return inventory.getAvailableQuantity() >= quantity;
    }

    @Transactional
    public void reserveStock(List<InventoryRequest> req) throws NotEnoughStockException {

        Map<String,Inventory> inventoryMap = getBySkuMap(req);
        System.out.println("point 1");
        for(InventoryRequest request : req){
            Inventory inventory = inventoryMap.get(request.getSku());

            if(inventory == null){
                System.out.println("getting null");
                throw  new NoProductFound("No Product Found with sku "+request.getSku());
            }

            if(inventory.getAvailableQuantity() >= request.getQuantity()){
                System.out.println("ok");
                inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity() );
            }
            else {
                System.out.println("getting error");
                throw new NotEnoughStockException("Not enough stock for SKU: " + request.getSku() +
                        ". Requested: " + request.getQuantity() + ", Available: " + inventory.getAvailableQuantity());
            }
        }
    }

    @Transactional
    public void releaseStock(List<InventoryRequest> requests){

        Map<String,Inventory> inventoryMap = getBySkuMap(requests);

        for(InventoryRequest req : requests){
            Inventory inventory = inventoryMap.get(req.getSku());

            if(inventory == null){
                throw  new NoProductFound("No Product Found with sku "+req.getSku());
            }

            Integer quantity = req.getQuantity();

            if( quantity > inventory.getReservedQuantity() ){
                throw new IllegalArgumentException("Cannot release more stock than is currently reserved for SKU: " + req.getSku());
            }

            inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity );
        }
    }

    @Transactional
    public void fulfillStock(List<InventoryRequest> requests){
        Map<String,Inventory> inventoryMap = getBySkuMap(requests);
        for(InventoryRequest req : requests){

            Inventory inventory = inventoryMap.get(req.getSku());

            if(inventory == null){
                throw  new NoProductFound("No Product Found with sku "+req.getSku());
            }

            Integer quantity = req.getQuantity();

            if( quantity > inventory.getReservedQuantity() ){
                throw new IllegalStateException("Cannot fulfill more stock than is reserved for SKU: " + req.getSku());
            }

            inventory.setReservedQuantity( inventory.getReservedQuantity() - quantity );
            inventory.setTotalQuantity(inventory.getTotalQuantity() - quantity);
        }
    }

    @Transactional
    public List<InventoryResponse> getStockDetails(List<String> productId){
        List<Inventory> inventories = repo.findAllByProductIdIn(productId);

        if (inventories.size() != productId.size()) {

            Set<String> foundProductIds = inventories.stream()
                    .map(Inventory::getProductId)
                    .collect(Collectors.toSet());


            List<String> missingIds = productId.stream()
                    .filter(id -> !foundProductIds.contains(id))
                    .toList();

            throw new NoProductFound("Products not found with IDs: " + missingIds);
        }

        return inventories.stream()
                .map(this::convertToResponse).toList();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public List<InventoryResponse> addStock(List<InventoryRequest > requests){

        Map<String,Inventory> inventoryMap = getBySkuMap(requests);

        List<Inventory> updatedInventories  = new ArrayList<>();
        List<Inventory> newInventoriesToSave = new ArrayList<>();

        for(InventoryRequest req : requests){
            Inventory inventory = inventoryMap.get(req.getSku());

            if(inventory == null){
                Inventory inv = new Inventory().builder()
                        .sku(req.getSku())
                        .productId(req.getProductId())
                        .totalQuantity(req.getQuantity())
                        .build();
                newInventoriesToSave.add(inv);

            }else{
                inventory.setTotalQuantity( inventory.getTotalQuantity() + req.getQuantity() );
                updatedInventories .add(inventory);
            }
        }

        if ( !newInventoriesToSave.isEmpty() ) {
            repo.saveAll(newInventoriesToSave);
        }

        List<Inventory> allAffectedInventories = new ArrayList<>();
        allAffectedInventories.addAll(updatedInventories);
        allAffectedInventories.addAll(newInventoriesToSave);

        List<InventoryResponse> list = allAffectedInventories.stream().map(this::convertToResponse).toList();
        return list;
    }

    private InventoryResponse convertToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .inventoryId(inventory.getInventoryId())
                .productId(inventory.getProductId())
                .sku(inventory.getSku())
                .reservedQuantity(inventory.getReservedQuantity())
                .totalQuantity(inventory.getTotalQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private Map<String , Inventory> getBySkuMap(List<InventoryRequest> requests){
        List<String> skus = requests.stream()
                .map(InventoryRequest::getSku)
                .toList();

        Map<String , Inventory> inventoryMap = repo.findBySkuIn(skus).stream()
                .collect(Collectors.toMap(req -> req.getSku() , inventory -> inventory));

        return inventoryMap;

    }
}
