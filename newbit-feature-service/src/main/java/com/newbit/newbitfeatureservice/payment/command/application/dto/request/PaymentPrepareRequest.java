package com.newbit.newbitfeatureservice.payment.command.application.dto.request;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareRequest {
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String orderName;
    private Long userId;
} 