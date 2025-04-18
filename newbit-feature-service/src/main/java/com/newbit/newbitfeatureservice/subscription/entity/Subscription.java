package com.newbit.newbitfeatureservice.subscription.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_list")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
@IdClass(SubscriptionId.class)
@Schema(description = "시리즈 구독 엔티티")
public class Subscription {

    @Id
    @Column(name = "user_id", nullable = false)
    @Schema(description = "사용자 ID")
    private Long userId;

    @Id
    @Column(name = "series_id", nullable = false)
    @Schema(description = "시리즈 ID")
    private Long seriesId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "구독 시작 시간", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Schema(description = "최종 상태 변경 시간", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
    
    public static Subscription createSubscription(Long userId, Long seriesId) {
        return Subscription.builder()
                .userId(userId)
                .seriesId(seriesId)
                .build();
    }
    
    public SubscriptionId getId() {
        return new SubscriptionId(userId, seriesId);
    }
} 