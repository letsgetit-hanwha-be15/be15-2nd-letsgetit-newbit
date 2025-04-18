package com.newbit.newbitfeatureservice.payment.command.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.payment.command.application.dto.PaymentDto;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentApproveResponse implements PaymentDto {
    private Long paymentId;
    private String orderId;
    private String paymentKey;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime approvedAt;
    private String receiptUrl;
    
    @Override
    public LocalDateTime getProcessedAt() {
        return approvedAt;
    }
} 