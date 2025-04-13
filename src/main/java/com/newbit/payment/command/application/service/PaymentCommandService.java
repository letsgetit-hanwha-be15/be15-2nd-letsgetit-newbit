package com.newbit.payment.command.application.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.payment.command.application.dto.request.PaymentApproveRequest;
import com.newbit.payment.command.application.dto.request.PaymentPrepareRequest;
import com.newbit.payment.command.application.dto.response.PaymentApproveResponse;
import com.newbit.payment.command.application.dto.response.PaymentPrepareResponse;
import com.newbit.payment.command.domain.aggregate.Payment;
import com.newbit.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.payment.command.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentApiClient tossPaymentApiClient;

    @Transactional
    public PaymentPrepareResponse preparePayment(PaymentPrepareRequest request) {
        String orderId = generateOrderId();
        
        Payment payment = Payment.createPayment(
                request.getAmount(),
                request.getPaymentMethod(),
                request.getUserId(),
                orderId,
                request.getOrderName()
        );
        
        Payment savedPayment = paymentRepository.save(payment);
        
        String paymentWidgetUrl = tossPaymentApiClient.createPaymentWidgetUrl(
                savedPayment.getAmount().longValue(),
                savedPayment.getOrderId(),
                savedPayment.getOrderName() != null ? savedPayment.getOrderName() : "상품 결제"
        );
        
        return PaymentPrepareResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrderId())
                .orderName(savedPayment.getOrderName())
                .amount(savedPayment.getAmount())
                .paymentMethod(savedPayment.getPaymentMethod())
                .paymentStatus(savedPayment.getPaymentStatus())
                .paymentWidgetUrl(paymentWidgetUrl)
                .build();
    }

    @Transactional
    public PaymentApproveResponse approvePayment(PaymentApproveRequest request) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        
        validateAmount(payment.getAmount(), BigDecimal.valueOf(request.getAmount()));
        
        TossPaymentApiDto.PaymentResponse tossResponse = tossPaymentApiClient.requestPaymentApproval(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
        );
        
        payment.updatePaymentKey(tossResponse.getPaymentKey());
        payment.approve(tossResponse.getApprovedAtDateTime());
        payment.updatePaymentStatus(PaymentStatus.DONE);
        
        if (tossResponse.getReceipt() != null && tossResponse.getReceipt().get("url") != null) {
            payment.updateReceiptUrl(tossResponse.getReceipt().get("url").toString());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        
        return PaymentApproveResponse.builder()
                .paymentId(updatedPayment.getPaymentId())
                .orderId(updatedPayment.getOrderId())
                .paymentKey(updatedPayment.getPaymentKey())
                .amount(updatedPayment.getAmount())
                .paymentMethod(updatedPayment.getPaymentMethod())
                .paymentStatus(updatedPayment.getPaymentStatus())
                .approvedAt(updatedPayment.getApprovedAt())
                .receiptUrl(updatedPayment.getReceiptUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentApproveResponse getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        
        return PaymentApproveResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .approvedAt(payment.getApprovedAt())
                .receiptUrl(payment.getReceiptUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public PaymentApproveResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        
        return PaymentApproveResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentKey(payment.getPaymentKey())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .approvedAt(payment.getApprovedAt())
                .receiptUrl(payment.getReceiptUrl())
                .build();
    }

    private void validateAmount(BigDecimal savedAmount, BigDecimal requestAmount) {
        if (savedAmount.compareTo(requestAmount) != 0) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    private String generateOrderId() {
        return UUID.randomUUID().toString();
    }
} 