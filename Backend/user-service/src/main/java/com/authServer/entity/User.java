package com.authServer.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyCloakId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL ,mappedBy = "user" , orphanRemoval = true)
    private List<Address> addresses;

    @OneToOne(cascade = CascadeType.ALL ,mappedBy = "user" , orphanRemoval = true)
    private Contact contact;

    @Builder.Default
    private Boolean isActive = true;
}
