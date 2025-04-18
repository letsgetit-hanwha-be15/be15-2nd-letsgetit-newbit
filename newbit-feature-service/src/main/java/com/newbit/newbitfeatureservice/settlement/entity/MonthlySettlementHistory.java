package com.newbit.newbitfeatureservice.settlement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_settlement_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MonthlySettlementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthlySettlementHistoryId;

    private int settlementYear;
    private int settlementMonth;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal settlementAmount;

    private LocalDateTime settledAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    public static MonthlySettlementHistory of(Long mentorId, int year, int month, BigDecimal amount) {
        return MonthlySettlementHistory.builder()
                .mentorId(mentorId)
                .settlementYear(year)
                .settlementMonth(month)
                .settlementAmount(amount)
                .settledAt(LocalDateTime.now())
                .build();
    }
}
