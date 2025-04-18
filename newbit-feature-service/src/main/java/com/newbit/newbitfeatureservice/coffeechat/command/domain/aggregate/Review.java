package com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;
    @Column(name = "rating", nullable = false)
    private BigDecimal rating;
    @Column(name = "comment")
    private String comment;
    @Column(name = "tip")
    private Integer tip;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "coffeechat_id", nullable = false)
    private Long coffeechatId;
    @Column(name = "user_id")
    private Long userId;

    public static Review of(@NotNull @Min(value = 1) @Max(value = 5) double rating, String comment, Integer tip, @Min(value = 1) Long coffeechatId, Long userId) {
        Review review = new Review();
        review.rating = BigDecimal.valueOf(rating);
        if(comment != null) review.comment = comment;
        if(tip != null) review.tip = tip;
        review.coffeechatId = coffeechatId;
        review.userId = userId;
        return review;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
