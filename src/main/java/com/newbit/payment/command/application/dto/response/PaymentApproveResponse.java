package com.newbit.payment.command.application.dto.response;

import com.newbit.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.payment.command.domain.aggregate.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentApproveResponse {
    private Long paymentId;
    private String orderId;
    private String paymentKey;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime approvedAt;
    private String receiptUrl;
} 