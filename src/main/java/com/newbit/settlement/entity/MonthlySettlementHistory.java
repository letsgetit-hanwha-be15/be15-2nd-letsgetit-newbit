package com.newbit.settlement.entity;

import com.newbit.user.entity.Mentor;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    public static MonthlySettlementHistory of(Mentor mentor, int year, int month, BigDecimal amount) {
        return MonthlySettlementHistory.builder()
                .mentor(mentor)
                .settlementYear(year)
                .settlementMonth(month)
                .settlementAmount(amount)
                .settledAt(LocalDateTime.now())
                .build();
    }
}
