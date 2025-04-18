package com.newbit.newbitfeatureservice.product.command.domain.aggregate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer diamondAmount;

    @Column(length = 500)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private Product(String name, BigDecimal price, Integer diamondAmount, String description, boolean isActive) {
        this.name = name;
        this.price = price;
        this.diamondAmount = diamondAmount;
        this.description = description;
        this.isActive = isActive;
    }

    public static Product createDiamondProduct(String name, BigDecimal price, Integer diamondAmount, String description) {
        return Product.builder()
                .name(name)
                .price(price)
                .diamondAmount(diamondAmount)
                .description(description)
                .isActive(true)
                .build();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void updateProduct(String name, BigDecimal price, Integer diamondAmount, String description) {
        this.name = name;
        this.price = price;
        this.diamondAmount = diamondAmount;
        this.description = description;
    }
} 