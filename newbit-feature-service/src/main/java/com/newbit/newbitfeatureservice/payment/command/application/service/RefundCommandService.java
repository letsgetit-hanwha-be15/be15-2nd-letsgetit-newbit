package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentRefundResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Refund;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.PaymentRepository;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.RefundRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.DiamondTransactionCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RefundCommandService extends AbstractPaymentService<PaymentRefundResponse> {

    private static final int DIAMOND_UNIT_PRICE = 100;
    private final RefundRepository refundRepository;
    private final NotificationCommandService notificationCommandService;
    private final DiamondTransactionCommandService diamondTransactionCommandService;

    public RefundCommandService(PaymentRepository paymentRepository,
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
        
        payment.refund();
        payment = paymentRepository.save(payment);
        
        Refund refund = Refund.createRefund(
                payment, 
                payment.getAmount(), 
                cancelReason, 
                response.getPaymentKey(),
                false
        );
        
        Refund savedRefund = refundRepository.save(refund);

        String notificationContent = String.format("환불이 완료되었습니다. (환불금액 : %,d)", savedRefund.getAmount().intValue());

        int refundDiamondAmount = savedRefund.getAmount().intValue() / DIAMOND_UNIT_PRICE;
        diamondTransactionCommandService.applyDiamondRefund(
                payment.getUserId(),
                savedRefund.getRefundId(),
                refundDiamondAmount
        );

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        payment.getUserId()
                        , 15L
                        , savedRefund.getRefundId()
                        , notificationContent
                )
        );
        
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
        
        payment.partialRefund(cancelAmount);
        payment = paymentRepository.save(payment);
        
        Refund refund = Refund.createRefund(
                payment, 
                cancelAmount, 
                cancelReason, 
                response.getPaymentKey(),
                true
        );
        
        Refund savedRefund = refundRepository.save(refund);

        int refundDiamondAmount = savedRefund.getAmount().intValue() / DIAMOND_UNIT_PRICE;
        diamondTransactionCommandService.applyDiamondRefund(
                payment.getUserId(),
                savedRefund.getRefundId(),
                refundDiamondAmount
        );

        String notificationContent = String.format("환불이 완료되었습니다. (환불금액 : %,d)", savedRefund.getAmount().intValue());


        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        payment.getUserId()
                        , 15L
                        , savedRefund.getRefundId()
                        , notificationContent
                )
        );

        return createRefundResponse(savedRefund);
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