package com.newbit.newbituserservice.user.entity;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private BigDecimal temperature;

    @Column(length = 255)
    private String preferredTime;

    private Boolean isActive;

    private Integer price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(length = 255)
    private String introduction;

    @Column(length = 2048)
    private String externalLinkUrl;

    public static Mentor createDefault(User user) {
        return Mentor.builder()
                .user(user)
                .price(200) // 기본 커피챗 가격
                .temperature(BigDecimal.valueOf(36.5)) // 초기 신뢰 온도 (정수로 변경)
                .isActive(true) // 기본 활성 상태
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void setExternalLinkUrl(String externalLinkUrl) {
        this.externalLinkUrl = externalLinkUrl;
    }
}