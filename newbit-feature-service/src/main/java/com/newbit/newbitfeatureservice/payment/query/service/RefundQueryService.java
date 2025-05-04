package com.newbit.newbitfeatureservice.payment.query.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Refund;
import com.newbit.newbitfeatureservice.payment.query.dto.RefundQueryDto;
import com.newbit.newbitfeatureservice.payment.query.repository.RefundQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefundQueryService {

    private final RefundQueryRepository refundQueryRepository;


    public Page<RefundQueryDto> getAllRefunds(Pageable pageable) {
        return refundQueryRepository.findAll(pageable)
                .map(this::mapToRefundQueryDto);
    }

    public Page<RefundQueryDto> getRefundsByPaymentId(Long paymentId, Pageable pageable) {
        return refundQueryRepository.findByPaymentId(paymentId, pageable)
                .map(this::mapToRefundQueryDto);
    }

    public Page<RefundQueryDto> getRefundsByUserId(Long userId, Pageable pageable) {
        return refundQueryRepository.findByUserId(userId, pageable)
                .map(this::mapToRefundQueryDto);
    }

    public Page<RefundQueryDto> getPartialRefundsByPaymentId(Long paymentId, Pageable pageable) {
        return refundQueryRepository.findPartialRefundsByPaymentId(paymentId, pageable)
                .map(this::mapToRefundQueryDto);
    }

    public RefundQueryDto getRefundDetail(Long refundId) {
        Refund refund = refundQueryRepository.findById(refundId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_FOUND));
        return mapToRefundQueryDto(refund);
    }

    private RefundQueryDto mapToRefundQueryDto(Refund refund) {
        return RefundQueryDto.builder()
                .refundId(refund.getRefundId())
                .paymentId(refund.getPayment().getPaymentId())
                .orderId(refund.getPayment().getOrderId())
                .paymentKey(refund.getPayment().getPaymentKey())
                .refundKey(refund.getRefundKey())
                .amount(refund.getAmount())
                .reason(refund.getReason())
                .refundedAt(refund.getRefundedAt())
                .isPartialRefund(refund.isPartialRefund())
                .bankCode(refund.getBankCode())
                .accountNumber(refund.getAccountNumber())
                .holderName(refund.getHolderName())
                .userId(refund.getPayment().getUserId())
                .build();
    }
} 