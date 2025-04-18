
package com.newbit.newbitfeatureservice.purchase.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "sale_history")
public class SaleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_history_id")
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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
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

    public static SaleHistory forColumn(Long columnId, Integer columnPrice, Long mentorId) {
        return SaleHistory.builder()
                .saleAmount(BigDecimal.valueOf(columnPrice * 100))
                .serviceType(ServiceType.COLUMN)
                .serviceId(columnId)
                .mentorId(mentorId)
                .build();
    }

    public static SaleHistory forCoffeechat(long mentorId, int totalPrice, long coffeechatId) {
        return SaleHistory.builder()
                .saleAmount(BigDecimal.valueOf(totalPrice * 100L))
                .serviceType(ServiceType.COFFEECHAT)
                .serviceId(coffeechatId)
                .mentorId(mentorId)
                .build();
    }

    public void markAsSettled() {
        this.settledAt = LocalDateTime.now();
        this.isSettled = true;
    }
}