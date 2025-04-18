package com.newbit.newbitfeatureservice.payment.command.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentDto {

    Long getPaymentId();
    
    BigDecimal getAmount();
    
    LocalDateTime getProcessedAt();
} 