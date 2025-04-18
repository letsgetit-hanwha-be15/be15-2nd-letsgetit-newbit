package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentApproveRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentPrepareRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentApproveResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentPrepareResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.PaymentRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.DiamondTransactionCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TossPaymentApiClient tossPaymentApiClient;

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private DiamondTransactionCommandService diamondTransactionCommandService;

    @InjectMocks
    private PaymentCommandService paymentCommandService;

    private PaymentPrepareRequest prepareRequest;
    private Payment payment;
    private Payment savedPayment;
    private PaymentApproveRequest approveRequest;
    private TossPaymentApiDto.PaymentResponse tossPaymentResponse;

    @BeforeEach
    void setUp() {
        prepareRequest = PaymentPrepareRequest.builder()
                .amount(BigDecimal.valueOf(10000))
                .paymentMethod(PaymentMethod.CARD)
                .orderName("테스트 상품")
                .userId(1L)
                .build();

        payment = Payment.createPayment(
                BigDecimal.valueOf(10000),
                PaymentMethod.CARD,
                1L,
                "test-order-id",
                "테스트 상품"
        );
        
        savedPayment = Payment.createPayment(
                BigDecimal.valueOf(10000),
                PaymentMethod.CARD,
                1L,
                "test-order-id",
                "테스트 상품"
        );
        try {
            java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentId");
            field.setAccessible(true);
            field.set(savedPayment, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        approveRequest = PaymentApproveRequest.builder()
                .paymentKey("test-payment-key")
                .orderId("test-order-id")
                .amount(10000L)
                .build();

        Map<String, Object> receiptMap = new HashMap<>();
        receiptMap.put("url", "https://receipt.url");
        
        tossPaymentResponse = TossPaymentApiDto.PaymentResponse.builder()
                .paymentKey("test-payment-key")
                .orderId("test-order-id")
                .status("DONE")
                .approvedAt("2023-01-01T12:00:00")
                .receipt(receiptMap)
                .build();
    }

    @Test
    @DisplayName("결제 준비 - 성공")
    void preparePayment_success() {
        // given
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(tossPaymentApiClient.createPaymentWidgetUrl(anyLong(), anyString(), anyString()))
                .thenReturn("https://widget.url");

        // when
        PaymentPrepareResponse response = paymentCommandService.preparePayment(prepareRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals("테스트 상품", response.getOrderName());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        assertEquals(PaymentMethod.CARD, response.getPaymentMethod());
        assertEquals(PaymentStatus.READY, response.getPaymentStatus());
        assertEquals("https://widget.url", response.getPaymentWidgetUrl());
        
        verify(paymentRepository).save(any(Payment.class));
        verify(tossPaymentApiClient).createPaymentWidgetUrl(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("결제 승인 - 성공")
    void approvePayment_success() {
        // given
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.of(payment));
        when(tossPaymentApiClient.requestPaymentApproval(anyString(), anyString(), anyLong()))
                .thenReturn(tossPaymentResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // when
        PaymentApproveResponse response = paymentCommandService.approvePayment(approveRequest);

        // then
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        assertEquals(PaymentMethod.CARD, response.getPaymentMethod());
        assertEquals(PaymentStatus.DONE, response.getPaymentStatus());
        assertNotNull(response.getApprovedAt());
        assertEquals("https://receipt.url", response.getReceiptUrl());
        
        verify(paymentRepository).findByOrderId(anyString());
        verify(tossPaymentApiClient).requestPaymentApproval(anyString(), anyString(), anyLong());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 승인 - 결제 내역 없음")
    void approvePayment_paymentNotFound() {
        // given
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.empty());

        // when, then
        assertThrows(BusinessException.class, () -> paymentCommandService.approvePayment(approveRequest));
        
        verify(paymentRepository).findByOrderId(anyString());
        verify(tossPaymentApiClient, never()).requestPaymentApproval(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("결제 승인 - 금액 불일치")
    void approvePayment_amountMismatch() {
        // given
        PaymentApproveRequest invalidAmountRequest = PaymentApproveRequest.builder()
                .paymentKey("test-payment-key")
                .orderId("test-order-id")
                .amount(20000L) // 다른 금액
                .build();
        
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.of(payment));

        // when, then
        assertThrows(BusinessException.class, () -> paymentCommandService.approvePayment(invalidAmountRequest));
        
        verify(paymentRepository).findByOrderId(anyString());
        verify(tossPaymentApiClient, never()).requestPaymentApproval(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("결제 조회 - 성공")
    void getPayment_success() {
        // given
        payment.updatePaymentKey("test-payment-key");
        payment.approve(LocalDateTime.now());
        payment.updateReceiptUrl("https://receipt.url");
        
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        // when
        PaymentApproveResponse response = paymentCommandService.getPayment(1L);

        // then
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        assertEquals("https://receipt.url", response.getReceiptUrl());
        
        verify(paymentRepository).findById(anyLong());
    }

    @Test
    @DisplayName("주문 ID로 결제 조회 - 성공")
    void getPaymentByOrderId_success() {
        // given
        payment.updatePaymentKey("test-payment-key");
        payment.approve(LocalDateTime.now());
        
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.of(payment));

        // when
        PaymentApproveResponse response = paymentCommandService.getPaymentByOrderId("test-order-id");

        // then
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        
        verify(paymentRepository).findByOrderId(anyString());
    }
} 