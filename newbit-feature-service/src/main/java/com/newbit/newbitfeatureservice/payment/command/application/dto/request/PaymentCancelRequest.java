package com.newbit.newbitfeatureservice.payment.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCancelRequest {
    private Long paymentId;
    private String reason;
    private BigDecimal cancelAmount;
} 