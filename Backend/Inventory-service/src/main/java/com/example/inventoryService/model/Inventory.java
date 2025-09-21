package com.example.inventoryService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    private String productId;
    @Column(unique = true , nullable = false)
    private String sku;

    @Builder.Default
    private long totalQuantity = 0;

    @Builder.Default
    private long reservedQuantity = 0;

    @Transient // Explicitly tells JPA to ignore this field for persistence
    public long getAvailableQuantity() {
        return this.totalQuantity - this.reservedQuantity;
    }

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
