package com.newbit.newbitfeatureservice.purchase.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long id;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_type_id", nullable = false)
    private PointType pointType;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    private PointHistory(Long serviceId, Integer balance, PointType pointType, Long userId) {
        this.serviceId = serviceId;
        this.balance = balance;
        this.pointType = pointType;
        this.userId = userId;
    }

    /**
     * 멘토 권한 구매용 히스토리 생성
     * PointType은 서비스 계층에서 조회해서 넘겨줘야 함 (예: ID = 5)
     */
    public static PointHistory forMentorAuthority(Long userId, PointType pointType, Integer point, Integer price) {
        return PointHistory.builder()
                .userId(userId)
                .pointType(pointType)
                .balance(point)  // 차감 이후 잔여 포인트
                .serviceId(null)
                .build();
    }

}