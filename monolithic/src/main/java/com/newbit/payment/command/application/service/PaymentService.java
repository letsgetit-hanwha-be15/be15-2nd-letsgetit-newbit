package com.newbit.payment.command.application.service;

import com.newbit.payment.command.application.dto.PaymentDto;

public interface PaymentService<T extends PaymentDto> {
    
    T getPayment(Long paymentId);
} 