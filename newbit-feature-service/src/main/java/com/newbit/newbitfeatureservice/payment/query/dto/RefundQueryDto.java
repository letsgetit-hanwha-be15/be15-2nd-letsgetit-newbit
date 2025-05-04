package com.newbit.newbitfeatureservice.payment.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundQueryDto {
    private Long refundId;
    private Long paymentId;
    private String orderId;
    private String paymentKey;
    private String refundKey;
    private BigDecimal amount;
    private String reason;
    private LocalDateTime refundedAt;
    private Boolean isPartialRefund;
    private String bankCode;
    private String accountNumber;
    private String holderName;
    private Long userId;
} 