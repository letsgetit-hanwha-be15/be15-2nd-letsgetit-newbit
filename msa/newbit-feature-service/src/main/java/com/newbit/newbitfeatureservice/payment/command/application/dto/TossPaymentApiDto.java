package com.newbit.newbitfeatureservice.payment.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class TossPaymentApiDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WidgetUrlRequest {
        private Long amount;
        private String orderId;
        private String orderName;
        private String successUrl;
        private String failUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfirmRequest {
        private String paymentKey;
        private String orderId;
        private Long amount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentResponse {
        private String version;
        private String paymentKey;
        private String type;
        private String orderId;
        private String orderName;
        private String mId;
        private String currency;
        private String method;
        private Long totalAmount;
        private Long balanceAmount;
        private String status;
        private String requestedAt;
        private String approvedAt;
        private Boolean useEscrow;
        private String lastTransactionKey;
        private Long suppliedAmount;
        private Long vat;
        private Boolean cultureExpense;
        private Long taxFreeAmount;
        private Long taxExemptionAmount;
        private Boolean isPartialCancelable;
        private Map<String, Object> card;
        private Map<String, Object> virtualAccount;
        private Map<String, Object> receipt;
        private Map<String, Object> checkout;
        private Map<String, Object> easyPay;
        private String country;
        private Map<String, Object> failure;
        
        public LocalDateTime getApprovedAtDateTime() {
            if (approvedAt == null || approvedAt.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(approvedAt.substring(0, 19));
        }
        
        public LocalDateTime getRequestedAtDateTime() {
            if (requestedAt == null || requestedAt.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(requestedAt.substring(0, 19));
        }
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCancelRequest {
        private String cancelReason;
        private Long cancelAmount;
        private Boolean refundableAmount;
        private String taxFreeAmount;
    }
} 