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
    private RefundReceiveAccount refundReceiveAccount;
    
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RefundReceiveAccount {
        private String bank;       // 은행 코드
        private String accountNumber; // 계좌번호
        private String holderName;    // 예금주명
    }
} 