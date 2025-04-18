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

    @Column(name = "is_refunded")
    private boolean isRefunded;
    
    @Column(name = "vat")
    private BigDecimal vat;
    
    @Column(name = "supplied_amount")
    private BigDecimal suppliedAmount;
    
    @Column(name = "tax_free_amount")
    private BigDecimal taxFreeAmount;
    
    @Column(name = "is_partial_cancelable")
    private boolean isPartialCancelable;
    
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
        
        // 기본값 설정
        if (this.totalAmount == null) {
            this.totalAmount = this.amount;
        }
        if (this.balanceAmount == null) {
            this.balanceAmount = this.amount;
        }
        if (this.currency == null) {
            this.currency = "KRW";
        }
        if (this.taxFreeAmount == null) {
            this.taxFreeAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private Payment(BigDecimal amount, PaymentMethod paymentMethod, String paymentKey, 
                   Long userId, PaymentStatus paymentStatus, String orderId, String orderName, 
                   LocalDateTime approvedAt, BigDecimal vat, BigDecimal suppliedAmount, 
                   BigDecimal taxFreeAmount, String receiptUrl, String currency,
                   boolean isPartialCancelable) {
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
        this.isRefunded = false;
        this.vat = vat;
        this.suppliedAmount = suppliedAmount;
        this.taxFreeAmount = taxFreeAmount != null ? taxFreeAmount : BigDecimal.ZERO;
        this.receiptUrl = receiptUrl;
        this.currency = currency != null ? currency : "KRW";
        this.isPartialCancelable = isPartialCancelable;
    }

    /**
     * 결제 준비를 위한 Payment 객체 생성
     */
    public static Payment createPayment(BigDecimal amount, PaymentMethod paymentMethod, 
                                      Long userId, String orderId, String orderName) {
        return Payment.builder()
                .amount(amount)
                .paymentMethod(paymentMethod)
                .userId(userId)
                .orderId(orderId)
                .orderName(orderName)
                .paymentStatus(PaymentStatus.READY)
                .isPartialCancelable(true)
                .build();
    }
    
    /**
     * 간단한 결제 생성 (이름 없는 주문)
     */
    public static Payment createPayment(BigDecimal amount, PaymentMethod paymentMethod, 
                                      Long userId, String orderId) {
        return createPayment(amount, paymentMethod, userId, orderId, null);
    }

    /**
     * 결제 승인
     */
    public void approve(LocalDateTime approvedAt) {
        this.paymentStatus = PaymentStatus.DONE;
        this.approvedAt = approvedAt;
    }

    /**
     * 결제 상태 업데이트
     */
    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    /**
     * 결제 키 업데이트
     */
    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
    
    /**
     * 영수증 URL 업데이트
     */
    public void updateReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
    
    /**
     * 전체 환불 처리
     */
    public void refund() {
        this.isRefunded = true;
        this.paymentStatus = PaymentStatus.CANCELED;
        this.balanceAmount = BigDecimal.ZERO;
    }
    
    /**
     * 부분 환불 처리
     */
    public void partialRefund(BigDecimal refundAmount) {
        if (!this.isPartialCancelable) {
            throw new IllegalStateException("해당 결제는 부분 환불이 불가능합니다");
        }
        
        if (refundAmount.compareTo(this.balanceAmount) > 0) {
            throw new IllegalArgumentException("환불 금액은 잔액보다 클 수 없습니다");
        }
        
        this.balanceAmount = this.balanceAmount.subtract(refundAmount);
        
        if (this.balanceAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.isRefunded = true;
            this.paymentStatus = PaymentStatus.CANCELED;
        } else {
            this.paymentStatus = PaymentStatus.PARTIAL_CANCELED;
        }
    }
} 