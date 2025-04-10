package com.newbit.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;

@Entity
@Table(name = "mentor")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mentorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer temperature;

    @Column(nullable = false, length = 255)
    private String preferredTime;

    private Boolean isActive;

    private Integer price;

    private java.time.LocalDateTime createdAt;

    private java.time.LocalDateTime updatedAt;

    public static Mentor createDefault(User user) {
        return Mentor.builder()
                .user(user)
                .price(500) // 기본 커피챗 가격
                .temperature(36) // 초기 신뢰 온도 (정수로 변경)
                .isActive(true) // 기본 활성 상태
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
    }
}