package com.newbit.newbitfeatureservice.payment.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "balance_amount")
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, unique = true)
    private String orderId;
    
    @Column(name = "order_name")
    private String orderName;

    @Column(unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column
    private LocalDateTime approvedAt;
    
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    
    @Column(name = "receipt_url")
    private String receiptUrl;
    
    @Column(length = 10)
    private String currency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.requestedAt = LocalDateTime.now();
        
        if (this.totalAmount == null) {
            this.totalAmount = this.amount;
        }
        if (this.balanceAmount == null) {
            this.balanceAmount = this.amount;
        }
        if (this.currency == null) {
            this.currency = "KRW";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Payment createPayment(BigDecimal amount, PaymentMethod paymentMethod, 
                                      Long userId, String orderId, String orderName) {
        return Payment.builder()
                .amount(amount)
                .paymentMethod(paymentMethod)
                .userId(userId)
                .orderId(orderId)
                .orderName(orderName)
                .paymentStatus(PaymentStatus.READY)
                .build();
    }

    public void approve(String paymentKey, LocalDateTime approvedAt, String receiptUrl) {
        this.paymentStatus = PaymentStatus.DONE;
        this.paymentKey = paymentKey;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }
    
    public boolean isPartialCancelable() {
        return this.paymentMethod == PaymentMethod.CARD;
    }
    
    public BigDecimal getBalanceAmount() {
        return this.balanceAmount;
    }
    
    public void updateAmountAfterPartialCancel(BigDecimal cancelAmount) {
        this.balanceAmount = this.balanceAmount.subtract(cancelAmount);
        this.paymentStatus = PaymentStatus.PARTIAL_CANCELED;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 