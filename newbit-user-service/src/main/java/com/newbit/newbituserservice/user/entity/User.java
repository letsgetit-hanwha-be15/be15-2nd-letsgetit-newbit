package com.newbit.newbituserservice.user.entity;

import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String nickname;

    @Builder.Default
    @Column(nullable = false)
    private Integer point = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer diamond = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority = Authority.USER;

    @Column(nullable = false)
    private Boolean isSuspended = false;

    private String profileImageUrl;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "job_id")
    private Long jobId;

    public void setJobId(Long jobId) {this.jobId = jobId;}

    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 보유 다이아 차감
    public void useDiamond(int amount) {
        if (this.diamond < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND);
        }
        this.diamond -= amount;
    }

    // 보유 다이아 증가
    public void addDiamond(int amount) {
        this.diamond += amount;
    }

    // 보유 포인트 증가
    public void addPoint(int amount) {
        this.point += amount;
    }

    // 보유 포인트 차감
    public void usePoint(int amount) {
        if (this.point < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND);
        }
        this.point -= amount;
    }

    // 권한이 멘토로 변경
    public void grantMentorAuthority() {
        if (this.getAuthority() == Authority.MENTOR) {
            throw new BusinessException(ErrorCode.ALREADY_MENTOR);
        }
        this.authority = Authority.MENTOR;
    }


    public void findPassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfileNicknameInfo(String nickname) {
        this.nickname = nickname;
    }
    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    public void updatePhonenumber(String phoneNumber){this.phoneNumber = phoneNumber;};

    public void setIsSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


}
