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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentCommandService extends AbstractPaymentService<PaymentApproveResponse> {

    public PaymentCommandService(PaymentRepository paymentRepository, TossPaymentApiClient tossPaymentApiClient) {
        super(paymentRepository, tossPaymentApiClient);
    }

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
        
        return createPaymentApproveResponse(updatedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentApproveResponse getPayment(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        return createPaymentApproveResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentApproveResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
        
        return createPaymentApproveResponse(payment);
    }


    private String generateOrderId() {
        return UUID.randomUUID().toString();
    }

    private PaymentApproveResponse createPaymentApproveResponse(Payment payment) {
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
} 