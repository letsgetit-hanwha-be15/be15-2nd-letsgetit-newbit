package com.newbit.newbitfeatureservice.payment.command.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.newbit.newbitfeatureservice.payment.command.application.dto.PaymentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRefundResponse implements PaymentDto {
    private Long refundId;
    private Long paymentId;
    private BigDecimal amount;
    private String reason;
    private String refundKey;
    private LocalDateTime refundedAt;
    private String bankCode;
    private String accountNumber;
    private String holderName;
    
    @Override
    public LocalDateTime getProcessedAt() {
        return refundedAt;
    }
} 