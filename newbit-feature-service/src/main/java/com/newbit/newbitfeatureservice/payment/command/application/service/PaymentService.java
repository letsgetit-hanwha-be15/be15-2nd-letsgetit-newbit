package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.payment.command.application.dto.PaymentDto;

public interface PaymentService<T extends PaymentDto> {
    
    T getPayment(Long paymentId);
} 