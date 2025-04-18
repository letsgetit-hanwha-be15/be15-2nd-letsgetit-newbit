package com.newbit.newbitfeatureservice.purchase.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_type_id")
    private Long pointTypeId;

    @Column(name = "point_type_name", nullable = false, length = 50, unique = true)
    private String pointTypeName;

    @Column(name = "increase_amount")
    private Integer increaseAmount;

    @Column(name = "decrease_amount")
    private Integer decreaseAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}