package com.newbit.purchase.command.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "column_purchase_history")
public class ColumnPurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private Column column;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ColumnPurchaseHistory of(User user, Column column) {
        ColumnPurchaseHistory history = new ColumnPurchaseHistory();
        history.user = user;
        history.column = column;
        history.price = column.getPrice();
        history.createdAt = LocalDateTime.now();
        history.updatedAt = LocalDateTime.now(); // DB에서도 업데이트 되지만 일단 맞춰둠
        return history;
    }
}