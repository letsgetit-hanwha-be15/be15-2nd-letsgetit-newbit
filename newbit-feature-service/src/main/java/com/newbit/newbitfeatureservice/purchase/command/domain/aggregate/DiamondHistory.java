package com.newbit.newbitfeatureservice.purchase.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "diamond_history")
public class DiamondHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diamond_history_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiamondTransactionType serviceType;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "decrease_amount")
    private Integer decreaseAmount;

    @Column(name = "increase_amount")
    private Integer increaseAmount;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Transient
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private DiamondHistory(Long userId, DiamondTransactionType serviceType, Long serviceId,
                           Integer decreaseAmount, Integer increaseAmount, Integer balance, String description) {
        this.userId = userId;
        this.serviceType = serviceType;
        this.serviceId = serviceId;
        this.decreaseAmount = decreaseAmount;
        this.increaseAmount = increaseAmount;
        this.balance = balance;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    // 칼럼 구매용 팩토리 메서드
    public static DiamondHistory forColumnPurchase(Long userId, Long columnId, Integer price, Integer diamondBalance) {
        return DiamondHistory.builder()
                .userId(userId)
                .serviceType(DiamondTransactionType.COLUMN)
                .serviceId(columnId)
                .decreaseAmount(price)
                .increaseAmount(null)
                .balance(diamondBalance)  // 차감 이후 잔액
                .build();
    }

    public static DiamondHistory forCoffeechatPurchase(Long userId, Long coffeechatId, Integer totalPrice, Integer diamondBalance) {
        return DiamondHistory.builder()
                .userId(userId)
                .serviceType(DiamondTransactionType.COFFEECHAT)
                .serviceId(coffeechatId)
                .decreaseAmount(totalPrice)
                .increaseAmount(null)
                .balance(diamondBalance)  // 차감 이후 잔액
                .build();
    }

    public static DiamondHistory forMentorAuthority(Long userId, Integer diamond, int mentorAuthorityDiamondCost) {
        return DiamondHistory.builder()
                .userId(userId)
                .serviceId(1L) //nullable로 변경
                .serviceType(DiamondTransactionType.MENTOR_AUTHORITY)
                .decreaseAmount(mentorAuthorityDiamondCost)
                .increaseAmount(null)
                .balance(diamond)  // 차감 이후 잔액
                .build();
    }

    public static DiamondHistory forCoffeechatRefund(Long userId, Long coffeechatId, int totalPrice, Integer diamondBalance) {
        return DiamondHistory.builder()
                .userId(userId)
                .serviceType(DiamondTransactionType.COFFEECHAT)
                .serviceId(coffeechatId)
                .decreaseAmount(null)
                .increaseAmount(totalPrice)
                .balance(diamondBalance)  // 차감 이후 잔액
                .build();
    }
}