package com.example.inventoryService.repo;

import com.example.inventoryService.model.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory , Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findBySkuIn(List<String> skus);

    List<Inventory> findAllByProductIdIn(List<String> productId);
}
