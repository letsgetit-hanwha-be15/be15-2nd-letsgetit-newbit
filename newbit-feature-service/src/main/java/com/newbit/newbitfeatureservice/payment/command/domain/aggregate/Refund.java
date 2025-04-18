package com.newbit.newbitfeatureservice.payment.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;
    
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(length = 200)
    private String reason;
    
    @Column(name = "refund_key")
    private String refundKey;
    
    @Column(name = "refunded_at", nullable = false)
    private LocalDateTime refundedAt;
    
    @Column(name = "bank_code", length = 10)
    private String bankCode;
    
    @Column(name = "account_number", length = 30)
    private String accountNumber;
    
    @Column(name = "holder_name", length = 50)
    private String holderName;
    
    @Column(name = "is_partial_refund", nullable = false)
    private boolean isPartialRefund;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.refundedAt == null) {
            this.refundedAt = LocalDateTime.now();
        }
    }
    
    @Builder
    private Refund(Payment payment, BigDecimal amount, String reason, String refundKey,
                 LocalDateTime refundedAt, String bankCode, String accountNumber,
                 String holderName, boolean isPartialRefund) {
        this.payment = payment;
        this.amount = amount;
        this.reason = reason;
        this.refundKey = refundKey;
        this.refundedAt = refundedAt != null ? refundedAt : LocalDateTime.now();
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.isPartialRefund = isPartialRefund;
    }
    
    public static Refund createRefund(Payment payment, BigDecimal amount, String reason, 
                                   String refundKey, boolean isPartialRefund) {
        return Refund.builder()
                .payment(payment)
                .amount(amount)
                .reason(reason)
                .refundKey(refundKey)
                .isPartialRefund(isPartialRefund)
                .build();
    }
} 