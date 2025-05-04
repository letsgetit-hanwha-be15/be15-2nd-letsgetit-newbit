package com.newbit.newbitfeatureservice.payment.query.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.query.dto.PaymentQueryDto;
import com.newbit.newbitfeatureservice.payment.query.repository.PaymentQueryRepository;
import com.newbit.newbitfeatureservice.payment.query.repository.RefundQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryService {

    private final PaymentQueryRepository paymentQueryRepository;
    private final RefundQueryRepository refundQueryRepository;

    /**
     * 전체 결제 내역 조회 (페이징)
     */
    public Page<PaymentQueryDto> getAllPayments(Pageable pageable) {
        return paymentQueryRepository.findAll(pageable)
                .map(this::mapToPaymentQueryDto);
    }

    /**
     * 사용자별 결제 내역 조회 (페이징)
     */
    public Page<PaymentQueryDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentQueryRepository.findByUserId(userId, pageable)
                .map(this::mapToPaymentQueryDto);
    }

    /**
     * 상태별 결제 내역 조회 (페이징)
     */
    public Page<PaymentQueryDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentQueryRepository.findByUserIdAndStatusIn(
                null, Arrays.asList(status), pageable)
                .map(this::mapToPaymentQueryDto);
    }

    /**
     * 사용자별 상태별 결제 내역 조회 (페이징)
     */
    public Page<PaymentQueryDto> getPaymentsByUserIdAndStatus(
            Long userId, List<PaymentStatus> statuses, Pageable pageable) {
        return paymentQueryRepository.findByUserIdAndStatusIn(userId, statuses, pageable)
                .map(this::mapToPaymentQueryDto);
    }

    /**
     * 환불 내역이 있는 결제 내역 조회 (페이징)
     */
    public Page<PaymentQueryDto> getPaymentsWithRefunds(Pageable pageable) {
        return paymentQueryRepository.findAllWithRefunds(pageable)
                .map(this::mapToPaymentQueryDto);
    }

    /**
     * 사용자별 환불 내역이 있는 결제 내역 조회 (페이징)
     */
    public Page<PaymentQueryDto> getPaymentsWithRefundsByUserId(Long userId, Pageable pageable) {
        return paymentQueryRepository.findByUserIdWithRefunds(userId, pageable)
                .map(this::mapToPaymentQueryDto);
    }

    /**
     * 결제 상세 정보 조회
     */
    public PaymentQueryDto getPaymentDetail(Long paymentId) {
        Payment payment = paymentQueryRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        return mapToPaymentQueryDto(payment);
    }

    /**
     * 주문번호 또는 결제키로 결제 정보 조회
     */
    public PaymentQueryDto getPaymentByOrderIdOrPaymentKey(String orderId, String paymentKey) {
        Payment payment = paymentQueryRepository.findByOrderIdOrPaymentKey(orderId, paymentKey);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return mapToPaymentQueryDto(payment);
    }

    /**
     * Payment 엔티티를 PaymentQueryDto로 변환
     */
    private PaymentQueryDto mapToPaymentQueryDto(Payment payment) {
        Long refundCount = paymentQueryRepository.countRefundsByPaymentId(payment.getPaymentId());
        Long totalRefundAmount = refundQueryRepository.getTotalRefundAmountByPaymentId(payment.getPaymentId());
        
        BigDecimal remainAmount = payment.getAmount();
        if (totalRefundAmount != null && totalRefundAmount > 0) {
            remainAmount = payment.getAmount().subtract(BigDecimal.valueOf(totalRefundAmount));
        }
        
        return PaymentQueryDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .userId(payment.getUserId())
                .orderName(payment.getOrderName())
                .approvedAt(payment.getApprovedAt())
                .requestedAt(payment.getRequestedAt())
                .receiptUrl(payment.getReceiptUrl())
                .hasRefund(refundCount > 0)
                .refundCount(refundCount)
                .remainAmount(remainAmount)
                .build();
    }
} 