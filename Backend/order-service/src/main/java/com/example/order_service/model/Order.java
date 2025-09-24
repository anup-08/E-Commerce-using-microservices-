package com.example.order_service.model;

import com.example.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PROCESSING;
    @CreationTimestamp
    private LocalDateTime orderAt;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY ,cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
