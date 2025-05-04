package com.newbit.newbitfeatureservice.payment.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentQueryDto {
    private Long paymentId;
    private String orderId;
    private String paymentKey;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Long userId;
    private String orderName;
    private LocalDateTime approvedAt;
    private LocalDateTime requestedAt;
    private String receiptUrl;
    private Boolean hasRefund; // 환불 내역 유무
    private Long refundCount; // 환불 횟수
    private BigDecimal remainAmount; // 부분 취소 후 남은 금액
} 