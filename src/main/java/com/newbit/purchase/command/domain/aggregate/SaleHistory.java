
package com.newbit.purchase.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sale_history")
public class SaleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_history_id")
    private Long id;

    @Column(name = "is_settled", nullable = false)
    private boolean isSettled = false;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "sale_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal saleAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Builder
    private SaleHistory(BigDecimal saleAmount, ServiceType serviceType, Long serviceId, Long mentorId) {
        this.saleAmount = saleAmount;
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.mentorId = mentorId;
        this.isSettled = false;
    }

    public static SaleHistory forColumn(com.newbit.column.entity.Column column) {
        return SaleHistory.builder()
                .saleAmount(BigDecimal.valueOf(column.getPrice()))
                .serviceType(ServiceType.COLUMN)
                .serviceId(column.getColumnId())
                .mentorId(column.getMentorId())
                .build();
    }

}