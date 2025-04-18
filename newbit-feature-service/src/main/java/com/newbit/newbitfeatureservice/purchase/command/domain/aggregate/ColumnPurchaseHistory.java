package com.newbit.newbitfeatureservice.purchase.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "column_purchase_history")
public class ColumnPurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "column_purchase_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "column_id", nullable = false)
    private Long columnId;

    @Column(name = "price", nullable = false)
    private Integer price;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ColumnPurchaseHistory of(Long userId, Long columnId, Integer price)
    {
        ColumnPurchaseHistory history = new ColumnPurchaseHistory();
        history.userId = userId;
        history.columnId = columnId;
        history.price = price;
        history.createdAt = LocalDateTime.now();
        history.updatedAt = LocalDateTime.now();
        return history;
    }
}