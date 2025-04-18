package com.newbit.newbitfeatureservice.payment.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentApproveRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
} 