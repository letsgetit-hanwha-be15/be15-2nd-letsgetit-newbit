package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentCancelRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentRefundResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Refund;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.PaymentRepository;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.RefundRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.DiamondTransactionCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundCommandService extends AbstractPaymentService<PaymentRefundResponse> {

    private static final int DIAMOND_UNIT_PRICE = 100;
    private final RefundRepository refundRepository;
    private final NotificationCommandService notificationCommandService;
    private final DiamondTransactionCommandService diamondTransactionCommandService;

    public RefundCommandService(
        PaymentRepository paymentRepository,
        RefundRepository refundRepository,
        TossPaymentApiClient tossPaymentApiClient,
        NotificationCommandService notificationCommandService,
        DiamondTransactionCommandService diamondTransactionCommandService
    ) {
        super(paymentRepository, tossPaymentApiClient);
        this.refundRepository = refundRepository;
        this.notificationCommandService = notificationCommandService;
        this.diamondTransactionCommandService = diamondTransactionCommandService;
    }

    @Transactional
    public PaymentRefundResponse cancelPayment(Long paymentId, String cancelReason) {
        Payment payment = findPaymentById(paymentId);
        validateCancelable(payment);
        
        TossPaymentApiDto.PaymentResponse response = tossPaymentApiClient.cancelPayment(
                payment.getPaymentKey(), 
                cancelReason
        );
        
        payment.cancel();
        payment = paymentRepository.save(payment);
        
        Refund refund = createRefundEntity(payment, payment.getAmount(), cancelReason, response);
        Refund savedRefund = refundRepository.save(refund);

        processRefundNotificationAndDiamonds(payment, savedRefund);
        
        return createRefundResponse(savedRefund);
    }
    
    @Transactional
    public PaymentRefundResponse cancelPaymentPartial(Long paymentId, BigDecimal cancelAmount, String cancelReason) {
        Payment payment = findPaymentById(paymentId);
        validatePartialCancelable(payment);
        validateRefundAmount(payment, cancelAmount);
        
        TossPaymentApiDto.PaymentResponse response = tossPaymentApiClient.cancelPaymentPartial(
                payment.getPaymentKey(), 
                cancelReason,
                cancelAmount.longValue()
        );
        
        payment.updatePaymentStatus(PaymentStatus.PARTIAL_CANCELED);
        payment = paymentRepository.save(payment);
        
        Refund refund = createRefundEntity(payment, cancelAmount, cancelReason, response);
        refund.setPartialRefund(true);
        Refund savedRefund = refundRepository.save(refund);

        processRefundNotificationAndDiamonds(payment, savedRefund);
        
        return createRefundResponse(savedRefund);
    }
    
    @Transactional
    public PaymentRefundResponse cancelVirtualAccountPayment(Long paymentId, PaymentCancelRequest request) {
        Payment payment = findPaymentById(paymentId);
        validateCancelable(payment);
        
        if (payment.getPaymentMethod() != PaymentMethod.VIRTUAL_ACCOUNT) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_VIRTUAL_ACCOUNT);
        }
        
        if (request.getRefundReceiveAccount() == null) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_ACCOUNT_REQUIRED);
        }
        
        PaymentCancelRequest.RefundReceiveAccount refundAccount = request.getRefundReceiveAccount();
        
        TossPaymentApiDto.PaymentResponse response;
        BigDecimal cancelAmount = request.getCancelAmount() != null ? 
                request.getCancelAmount() : payment.getAmount();
        
        response = tossPaymentApiClient.cancelVirtualAccountPayment(
                payment.getPaymentKey(),
                request.getReason(),
                cancelAmount.longValue(),
                refundAccount.getBank(),
                refundAccount.getAccountNumber(),
                refundAccount.getHolderName()
        );
        
        boolean isPartialCancel = request.getCancelAmount() != null && 
                payment.getAmount().compareTo(request.getCancelAmount()) > 0;
        
        if (isPartialCancel) {
            payment.updatePaymentStatus(PaymentStatus.PARTIAL_CANCELED);
        } else {
            payment.cancel();
        }
        
        payment = paymentRepository.save(payment);
        
        Refund refund = createRefundEntity(payment, cancelAmount, request.getReason(), response);
        refund.updateRefundAccountInfo(
                refundAccount.getBank(),
                refundAccount.getAccountNumber(),
                refundAccount.getHolderName()
        );
        refund.setPartialRefund(isPartialCancel);
        
        Refund savedRefund = refundRepository.save(refund);
        processRefundNotificationAndDiamonds(payment, savedRefund);
        
        return createRefundResponse(savedRefund);
    }

    @Transactional
    public PaymentRefundResponse cancelPaymentByKey(String paymentKey, String reason) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
                
        if (payment.getPaymentStatus() == PaymentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }
        
        try {
            TossPaymentApiDto.RefundResponse refundResponse = tossPaymentApiClient.requestPaymentCancel(
                    paymentKey, reason, null, null);
            
            payment.cancel();
            Payment updatedPayment = paymentRepository.save(payment);
            
            Refund refund = Refund.createRefund(
                    updatedPayment, 
                    updatedPayment.getAmount(), 
                    reason,
                    refundResponse.getRefundKey(),
                    false
            );
            
            Refund savedRefund = refundRepository.save(refund);
            
            return PaymentRefundResponse.builder()
                    .refundId(savedRefund.getRefundId())
                    .paymentId(updatedPayment.getPaymentId())
                    .amount(savedRefund.getAmount())
                    .reason(savedRefund.getReason())
                    .refundKey(savedRefund.getRefundKey())
                    .refundedAt(savedRefund.getRefundedAt())
                    .build();
                    
        } catch (Exception e) {
            log.error("결제 취소 요청 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PAYMENT_CANCEL_FAILED);
        }
    }
    
    private Refund createRefundEntity(Payment payment, BigDecimal amount, String reason, TossPaymentApiDto.PaymentResponse response) {
        String transactionKey = null;
        
        if (response.getCancels() != null && !response.getCancels().isEmpty()) {
            Map<String, Object> latestCancel = response.getCancels().get(response.getCancels().size() - 1);
            if (latestCancel.containsKey("transactionKey")) {
                transactionKey = (String) latestCancel.get("transactionKey");
            }
        }
        
        return Refund.builder()
                .payment(payment)
                .amount(amount)
                .reason(reason)
                .refundKey(transactionKey != null ? transactionKey : response.getPaymentKey())
                .isPartialRefund(false)
                .build();
    }
    
    private void processRefundNotificationAndDiamonds(Payment payment, Refund refund) {
        try {
            int refundDiamondAmount = refund.getAmount().intValue() / DIAMOND_UNIT_PRICE;
            
            diamondTransactionCommandService.applyDiamondRefund(
                    payment.getUserId(),
                    refund.getRefundId(),
                    refundDiamondAmount
            );
            
            String notificationContent = String.format("환불이 완료되었습니다. (환불금액 : %,d)", refund.getAmount().intValue());
            
            notificationCommandService.sendNotification(
                    new NotificationSendRequest(
                            payment.getUserId(),
                            15L,
                            refund.getRefundId(),
                            notificationContent
                    )
            );
        } catch (Exception e) {
            log.error("환불 후처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<PaymentRefundResponse> getRefundsByPaymentId(Long paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        
        List<Refund> refunds = refundRepository.findByPaymentPaymentId(paymentId);
        
        return refunds.stream()
                .map(this::createRefundResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentRefundResponse getPayment(Long paymentId) {
        List<Refund> refunds = refundRepository.findByPaymentPaymentId(paymentId);
        
        if (refunds.isEmpty()) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_REFUNDABLE);
        }
        
        return createRefundResponse(refunds.get(refunds.size() - 1));
    }
   
    @Transactional(readOnly = true)
    public PaymentRefundResponse getRefund(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_FOUND));
        
        return createRefundResponse(refund);
    }
    
    private PaymentRefundResponse createRefundResponse(Refund refund) {
        return PaymentRefundResponse.builder()
                .refundId(refund.getRefundId())
                .paymentId(refund.getPayment().getPaymentId())
                .amount(refund.getAmount())
                .reason(refund.getReason())
                .refundKey(refund.getRefundKey())
                .refundedAt(refund.getRefundedAt())
                .bankCode(refund.getBankCode())
                .accountNumber(refund.getAccountNumber())
                .holderName(refund.getHolderName())
                .build();
    }
} 