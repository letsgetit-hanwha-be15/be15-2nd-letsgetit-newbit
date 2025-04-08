package com.newbit.purchase.command.domain;

import com.newbit.purchase.command.domain.aggregate.DiamondTransactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public static DiamondHistory forColumnPurchase(User user, Column column) {
        return DiamondHistory.builder()
                .userId(user.getUserId())
                .serviceType(DiamondTransactionType.COLUMN)
                .serviceId(column.getColumnId())
                .decreaseAmount(column.getPrice())
                .increaseAmount(null)
                .balance(user.getDiamond())  // 차감 이후 잔액
                .build();
    }

    // 커피챗 구매용 팩토리 메서드
    public static DiamondHistory forCoffeechatPurchase(User user, Coffeechat coffeeChat, int totalPrice) {
        return DiamondHistory.builder()
                .userId(user.getUserId())
                .serviceType(DiamondTransactionType.COFFEE_CHAT)
                .serviceId(coffeeChat.getCoffeechatId())
                .decreaseAmount(totalPrice)
                .increaseAmount(null)
                .balance(user.getDiamond())  // 차감 이후 잔액
                .build();
    }

    public static DiamondHistory forMentorAuthority(User user, int mentorAuthorityDiamondCost) {
        return DiamondHistory.builder()
                .userId(user.getUserId())
                .serviceType(DiamondTransactionType.MENTOR_AUTHORITY)
                .decreaseAmount(mentorAuthorityDiamondCost)
                .increaseAmount(null)
                .balance(user.getDiamond())  // 차감 이후 잔액
                .build();
    }
}