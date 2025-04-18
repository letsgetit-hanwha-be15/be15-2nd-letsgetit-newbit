package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentRefundResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Payment;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentMethod;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.Refund;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.PaymentRepository;
import com.newbit.newbitfeatureservice.payment.command.domain.repository.RefundRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundCommandServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private TossPaymentApiClient tossPaymentApiClient;

    @Mock
    private NotificationCommandService notificationCommandService;

    @Mock
    private DiamondTransactionCommandService diamondTransactionCommandService;

    @InjectMocks
    private RefundCommandService refundCommandService;

    private Payment payment;
    private Refund refund;
    private Refund savedRefund;
    private TossPaymentApiDto.PaymentResponse tossPaymentResponse;

    @BeforeEach
    void setUp() {
        payment = createTestPayment();
        refund = createTestRefund(payment);
        savedRefund = createTestRefund(payment);
        setRefundId(savedRefund, 1L);
        
        Map<String, Object> receiptMap = new HashMap<>();
        receiptMap.put("url", "https://receipt.url");
        
        tossPaymentResponse = TossPaymentApiDto.PaymentResponse.builder()
                .paymentKey("test-refund-key")
                .orderId("test-order-id")
                .status("CANCELED")
                .approvedAt("2023-01-01T12:00:00")
                .receipt(receiptMap)
                .build();
    }

    @Test
    @DisplayName("전체 환불 - 성공")
    void cancelPayment_success() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(tossPaymentApiClient.cancelPayment(anyString(), anyString())).thenReturn(tossPaymentResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(refundRepository.save(any(Refund.class))).thenReturn(savedRefund);

        PaymentRefundResponse response = refundCommandService.cancelPayment(1L, "환불 테스트");

        assertNotNull(response);
        assertEquals(1L, response.getRefundId());
        assertEquals(1L, response.getPaymentId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        assertEquals("환불 테스트", response.getReason());
        
        verify(paymentRepository).findById(anyLong());
        verify(tossPaymentApiClient).cancelPayment(anyString(), anyString());
        verify(paymentRepository).save(any(Payment.class));
        verify(refundRepository).save(any(Refund.class));
    }

    @Test
    @DisplayName("전체 환불 - 결제 내역 없음")
    void cancelPayment_paymentNotFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> refundCommandService.cancelPayment(1L, "환불 테스트"));
        
        verify(paymentRepository).findById(anyLong());
        verify(tossPaymentApiClient, never()).cancelPayment(anyString(), anyString());
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(refundRepository, never()).save(any(Refund.class));
    }
    
    @Test
    @DisplayName("전체 환불 - 취소 불가능한 상태")
    void cancelPayment_cannotBeCancelled() {
        setPaymentStatus(payment, PaymentStatus.CANCELED);
        
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        assertThrows(BusinessException.class, () -> refundCommandService.cancelPayment(1L, "환불 테스트"));
        
        verify(paymentRepository).findById(anyLong());
        verify(tossPaymentApiClient, never()).cancelPayment(anyString(), anyString());
    }

    @Test
    @DisplayName("부분 환불 - 성공")
    void cancelPaymentPartial_success() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        when(tossPaymentApiClient.cancelPaymentPartial(anyString(), anyString(), anyLong())).thenReturn(tossPaymentResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(refundRepository.save(any(Refund.class))).thenReturn(savedRefund);

        PaymentRefundResponse response = refundCommandService.cancelPaymentPartial(1L, BigDecimal.valueOf(5000), "부분 환불 테스트");

        assertNotNull(response);
        assertEquals(1L, response.getRefundId());
        assertEquals(1L, response.getPaymentId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        assertEquals("환불 테스트", response.getReason());
        
        verify(paymentRepository).findById(anyLong());
        verify(tossPaymentApiClient).cancelPaymentPartial(anyString(), anyString(), anyLong());
        verify(paymentRepository).save(any(Payment.class));
        verify(refundRepository).save(any(Refund.class));
    }
    
    @Test
    @DisplayName("부분 환불 - 금액 초과")
    void cancelPaymentPartial_amountExceeds() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));

        assertThrows(BusinessException.class, () -> 
            refundCommandService.cancelPaymentPartial(1L, BigDecimal.valueOf(20000), "금액 초과 환불 테스트")
        );
        
        verify(paymentRepository).findById(anyLong());
        verify(tossPaymentApiClient, never()).cancelPaymentPartial(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("환불 내역 조회 - 성공")
    void getRefundsByPaymentId_success() {
        when(paymentRepository.existsById(anyLong())).thenReturn(true);
        when(refundRepository.findByPaymentPaymentId(anyLong())).thenReturn(Arrays.asList(refund, savedRefund));

        List<PaymentRefundResponse> responses = refundCommandService.getRefundsByPaymentId(1L);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        verify(paymentRepository).existsById(anyLong());
        verify(refundRepository).findByPaymentPaymentId(anyLong());
    }
    
    @Test
    @DisplayName("환불 내역 조회 - 결제 내역 없음")
    void getRefundsByPaymentId_paymentNotFound() {
        when(paymentRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BusinessException.class, () -> refundCommandService.getRefundsByPaymentId(1L));
        
        verify(paymentRepository).existsById(anyLong());
        verify(refundRepository, never()).findByPaymentPaymentId(anyLong());
    }

    @Test
    @DisplayName("결제 ID로 최근 환불 내역 조회 - 성공")
    void getPayment_success() {
        when(refundRepository.findByPaymentPaymentId(anyLong())).thenReturn(Arrays.asList(refund, savedRefund));

        PaymentRefundResponse response = refundCommandService.getPayment(1L);

        assertNotNull(response);
        assertEquals(1L, response.getRefundId());
        
        verify(refundRepository).findByPaymentPaymentId(anyLong());
    }
    
    @Test
    @DisplayName("결제 ID로 최근 환불 내역 조회 - 환불 내역 없음")
    void getPayment_refundNotFound() {
        when(refundRepository.findByPaymentPaymentId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(BusinessException.class, () -> refundCommandService.getPayment(1L));
        
        verify(refundRepository).findByPaymentPaymentId(anyLong());
    }

    @Test
    @DisplayName("환불 ID로 환불 내역 조회 - 성공")
    void getRefund_success() {
        when(refundRepository.findById(anyLong())).thenReturn(Optional.of(savedRefund));

        PaymentRefundResponse response = refundCommandService.getRefund(1L);

        assertNotNull(response);
        assertEquals(1L, response.getRefundId());
        assertEquals(1L, response.getPaymentId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        
        verify(refundRepository).findById(anyLong());
    }
    
    @Test
    @DisplayName("환불 ID로 환불 내역 조회 - 환불 내역 없음")
    void getRefund_refundNotFound() {
        when(refundRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> refundCommandService.getRefund(1L));
        
        verify(refundRepository).findById(anyLong());
    }
    
    private Payment createTestPayment() {
        Payment testPayment = Payment.createPayment(
                BigDecimal.valueOf(10000),
                PaymentMethod.CARD,
                1L,
                "test-order-id",
                "테스트 상품"
        );
        
        setPaymentId(testPayment, 1L);
        setPaymentKey(testPayment, "test-payment-key");
        setPaymentStatus(testPayment, PaymentStatus.DONE);
        setBalanceAmount(testPayment, BigDecimal.valueOf(10000));
        
        return testPayment;
    }
    
    private Refund createTestRefund(Payment payment) {
        return Refund.createRefund(
                payment,
                BigDecimal.valueOf(10000),
                "환불 테스트",
                "test-refund-key",
                false
        );
    }
    
    private void setPaymentId(Payment payment, Long id) {
        try {
            java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentId");
            field.setAccessible(true);
            field.set(payment, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setPaymentKey(Payment payment, String paymentKey) {
        try {
            java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentKey");
            field.setAccessible(true);
            field.set(payment, paymentKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setPaymentStatus(Payment payment, PaymentStatus status) {
        try {
            java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentStatus");
            field.setAccessible(true);
            field.set(payment, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setBalanceAmount(Payment payment, BigDecimal amount) {
        try {
            java.lang.reflect.Field field = Payment.class.getDeclaredField("balanceAmount");
            field.setAccessible(true);
            field.set(payment, amount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setRefundId(Refund refund, Long id) {
        try {
            java.lang.reflect.Field field = Refund.class.getDeclaredField("refundId");
            field.setAccessible(true);
            field.set(refund, id);
            
            java.lang.reflect.Field refundedAtField = Refund.class.getDeclaredField("refundedAt");
            refundedAtField.setAccessible(true);
            refundedAtField.set(refund, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
} 