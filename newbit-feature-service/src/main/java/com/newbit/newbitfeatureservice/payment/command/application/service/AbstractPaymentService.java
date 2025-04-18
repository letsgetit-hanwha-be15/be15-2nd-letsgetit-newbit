package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.payment.command.application.dto.PaymentDto;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public abstract class AbstractPaymentService<T extends PaymentDto> implements PaymentService<T> {

    protected final PaymentRepository paymentRepository;
    protected final TossPaymentApiClient tossPaymentApiClient;

    protected Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    protected void validateCancelable(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.DONE) {
            throw new BusinessException(ErrorCode.PAYMENT_CANNOT_BE_CANCELLED);
        }
    }

    protected void validatePartialCancelable(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.DONE && 
            payment.getPaymentStatus() != PaymentStatus.PARTIAL_CANCELED) {
            throw new BusinessException(ErrorCode.PAYMENT_CANNOT_BE_CANCELLED);
        }
        
        if (!payment.isPartialCancelable()) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PARTIAL_CANCELABLE);
        }
    }

    protected void validateRefundAmount(Payment payment, BigDecimal cancelAmount) {
        if (cancelAmount.compareTo(payment.getBalanceAmount()) > 0) {
            throw new BusinessException(ErrorCode.REFUND_AMOUNT_EXCEEDS_BALANCE);
        }
    }
    
    protected void validateAmount(BigDecimal savedAmount, BigDecimal requestAmount) {
        if (savedAmount.compareTo(requestAmount) != 0) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }
} 