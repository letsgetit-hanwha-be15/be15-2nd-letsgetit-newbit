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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(nullable = false)
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

    @Builder
    private Payment(BigDecimal amount, PaymentMethod paymentMethod, String paymentKey, 
                   Long userId, PaymentStatus paymentStatus, String orderId, String orderName, 
                   LocalDateTime approvedAt, String receiptUrl, String currency) {
        this.amount = amount;
        this.totalAmount = amount;
        this.balanceAmount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentKey = paymentKey;
        this.userId = userId;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.READY;
        this.orderId = orderId;
        this.orderName = orderName;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
        this.currency = currency != null ? currency : "KRW";
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

    /**
     * 테스트용 메소드 - approvedAt만 설정
     */
    public void approve(LocalDateTime approvedAt) {
        this.paymentStatus = PaymentStatus.DONE;
        this.approvedAt = approvedAt;
    }
    
    /**
     * 테스트용 메소드 - paymentKey 업데이트
     */
    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
    
    /**
     * 테스트용 메소드 - receiptUrl 업데이트
     */
    public void updateReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }
    
    /**
     * 부분 취소 가능 여부 확인
     * 카드 결제의 경우만 부분 취소 가능
     * @return 부분 취소 가능 여부
     */
    public boolean isPartialCancelable() {
        return this.paymentMethod == PaymentMethod.CARD;
    }
    
    /**
     * 잔액 조회
     * 전체 금액에서 부분 취소된 금액을 제외한 잔액 반환
     * @return 취소 가능한 잔액
     */
    public BigDecimal getBalanceAmount() {
        return this.balanceAmount;
    }
    
    /**
     * 결제 금액 업데이트 (부분 취소 후)
     * @param cancelAmount 취소된 금액
     */
    public void updateAmountAfterPartialCancel(BigDecimal cancelAmount) {
        this.balanceAmount = this.balanceAmount.subtract(cancelAmount);
        this.paymentStatus = PaymentStatus.PARTIAL_CANCELED;
    }
} 