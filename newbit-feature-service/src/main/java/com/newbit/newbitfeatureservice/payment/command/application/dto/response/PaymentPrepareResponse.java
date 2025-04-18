package com.newbit.newbitfeatureservice.payment.command.application.dto.response;

import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareResponse {
    private Long paymentId;
    private String orderId;
    private String orderName;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentWidgetUrl;
} 