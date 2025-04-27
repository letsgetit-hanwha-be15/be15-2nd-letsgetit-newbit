package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.notification.command.application.dto.request.NotificationSendRequest;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentApproveRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentPrepareRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentApproveResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentPrepareResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.PaymentRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.DiamondTransactionCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentCommandService extends AbstractPaymentService<PaymentApproveResponse> {

    private static final int DIAMOND_UNIT_PRICE = 100;
    private final NotificationCommandService notificationCommandService;
    private final DiamondTransactionCommandService diamondTransactionCommandService;

    public PaymentCommandService(PaymentRepository paymentRepository, 
                               TossPaymentApiClient tossPaymentApiClient,
                               NotificationCommandService notificationCommandService,
                               DiamondTransactionCommandService diamondTransactionCommandService) {
        super(paymentRepository, tossPaymentApiClient);
        this.notificationCommandService = notificationCommandService;
        this.diamondTransactionCommandService = diamondTransactionCommandService;
    }

    @Transactional
    public PaymentApproveResponse processPaymentSuccess(
            String paymentKey, 
            String orderId, 
            String orderName,
            BigDecimal amount, 
            PaymentMethod paymentMethod, 
            Long userId,
            String receiptUrl) {
            
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> Payment.createPayment(
                        amount,
                        paymentMethod,
                        userId,
                        orderId,
                        orderName
                ));
                
        payment.approve(paymentKey, LocalDateTime.now(), receiptUrl);
        
        Payment updatedPayment = paymentRepository.save(payment);

        int diamondAmount = updatedPayment.getAmount().intValue() / DIAMOND_UNIT_PRICE;
        diamondTransactionCommandService.applyDiamondPayment(
                updatedPayment.getUserId(),
                updatedPayment.getPaymentId(),
                diamondAmount
        );

        String notificationContent = String.format("결제가 완료 되었습니다. (결제 금액: %,d)", updatedPayment.getAmount().intValue());
        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        updatedPayment.getUserId(),
                        14L,
                        payment.getPaymentId(),
                        notificationContent
                )
        );
        
        return createPaymentApproveResponse(updatedPayment);
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
        
        payment.approve(tossResponse.getPaymentKey(), tossResponse.getApprovedAtDateTime(), 
                tossResponse.getReceipt() != null && tossResponse.getReceipt().get("url") != null ? 
                tossResponse.getReceipt().get("url").toString() : null);
        
        Payment updatedPayment = paymentRepository.save(payment);

        int diamondAmount = updatedPayment.getAmount().intValue() / DIAMOND_UNIT_PRICE;

        diamondTransactionCommandService.applyDiamondPayment(
                updatedPayment.getUserId(),
                updatedPayment.getPaymentId(),
                diamondAmount
        );

        String notificationContent = String.format("결제가 완료 되었습니다. (결제 금액: %,d)", updatedPayment.getAmount().intValue());

        notificationCommandService.sendNotification(
                new NotificationSendRequest(
                        updatedPayment.getUserId(),
                        14L,
                        payment.getPaymentId(),
                        notificationContent
                )
        );
        
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