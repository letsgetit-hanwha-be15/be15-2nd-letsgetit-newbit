package com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate;

import com.newbit.newbitfeatureservice.coffeechat.query.dto.response.ProgressStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "coffeechat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Coffeechat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coffeechat_id")
    private Long coffeechatId;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status", nullable = false)
    private ProgressStatus progressStatus = ProgressStatus.IN_PROGRESS;
    @Column(name = "request_message", nullable = false)
    private String requestMessage;
    @Column(name = "confirmed_schedule")
    private LocalDateTime confirmedSchedule;
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "purchase_confirmed_at")
    private LocalDateTime purchaseConfirmedAt;
    @Column(name = "sale_confirmed_at")
    private LocalDateTime saleConfirmedAt;
    @Column(name = "purchase_quantity", nullable = false)
    private int purchaseQuantity;
    @Column(name = "mentee_id")
    private Long menteeId;
    @Column(name = "mentor_id")
    private Long mentorId;
    @Column(name = "cancel_reason_id")
    private Long cancelReasonId;

    public static Coffeechat of(Long userId, @NotNull @Min(value = 1) Long mentorId, @NotNull @NotBlank String requestMessage, int purchaseQuantity) {
        Coffeechat coffeechat = new Coffeechat();
        coffeechat.menteeId = userId;
        coffeechat.mentorId = mentorId;
        coffeechat.requestMessage = requestMessage;
        coffeechat.purchaseQuantity = purchaseQuantity;

        return coffeechat;
    }

    public void markAsPurchased() {
        this.progressStatus = ProgressStatus.COFFEECHAT_WAITING;
    }

    public void confirmSchedule(LocalDateTime confirmedSchedule) {
        this.confirmedSchedule = confirmedSchedule;
        this.endedAt = confirmedSchedule.plusMinutes(purchaseQuantity * 30L);
        progressStatus = ProgressStatus.PAYMENT_WAITING;
    }

    public void rejectSchedule() {
        progressStatus = ProgressStatus.CANCEL;
        cancelReasonId = 3L;
    }

    public void closeSchedule() {
        progressStatus = ProgressStatus.COMPLETE;
        saleConfirmedAt = LocalDateTime.now();
    }

    public void confirmPurchaseSchedule() {
        purchaseConfirmedAt = LocalDateTime.now();
    }

    public void cancelCoffeechat(Long cancelReasonId) {
        progressStatus = ProgressStatus.CANCEL;
        this.cancelReasonId = cancelReasonId;
    }
}