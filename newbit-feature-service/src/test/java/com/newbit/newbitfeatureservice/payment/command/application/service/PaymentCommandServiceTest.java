package com.newbit.newbitfeatureservice.payment.command.application.service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.notification.command.application.service.NotificationCommandService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCommandServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private NotificationCommandService notificationCommandService;
    @Mock
    private DiamondTransactionCommandService diamondTransactionCommandService;
    @InjectMocks
    private PaymentCommandService paymentCommandService;

    private Payment payment;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
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
        setPaymentId(savedPayment, 1L);
    }

    @Test
    @DisplayName("결제 성공 처리 - 기존 결제 없음")
    void processPaymentSuccess_newPayment() {
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        assertDoesNotThrow(() -> paymentCommandService.processPaymentSuccess(
                "test-payment-key", "test-order-id", "테스트상품",
                BigDecimal.valueOf(10000), PaymentMethod.CARD, 1L, "https://receipt.url"
        ));
        verify(paymentRepository).findByOrderId(anyString());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 성공 처리 - 기존 결제 있음")
    void processPaymentSuccess_existingPayment() {
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        assertDoesNotThrow(() -> paymentCommandService.processPaymentSuccess(
                "test-payment-key", "test-order-id", "테스트상품",
                BigDecimal.valueOf(10000), PaymentMethod.CARD, 1L, "https://receipt.url"
        ));
        verify(paymentRepository).findByOrderId(anyString());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 상세 조회 - 성공")
    void getPayment_success() {
        payment.approve("test-payment-key", LocalDateTime.now(), "https://receipt.url");
        setPaymentId(payment, 1L);
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
        var response = paymentCommandService.getPayment(1L);
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        assertEquals("https://receipt.url", response.getReceiptUrl());
        verify(paymentRepository).findById(anyLong());
    }

    @Test
    @DisplayName("주문 ID로 결제 상세 조회 - 성공")
    void getPaymentByOrderId_success() {
        payment.approve("test-payment-key", LocalDateTime.now(), null);
        setPaymentId(payment, 1L);
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.of(payment));
        var response = paymentCommandService.getPaymentByOrderId("test-order-id");
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals(BigDecimal.valueOf(10000), response.getAmount());
        verify(paymentRepository).findByOrderId(anyString());
    }

    @Test
    @DisplayName("결제 상세 조회 - 결제 없음")
    void getPayment_notFound() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> paymentCommandService.getPayment(1L));
        verify(paymentRepository).findById(anyLong());
    }

    @Test
    @DisplayName("주문 ID로 결제 상세 조회 - 결제 없음")
    void getPaymentByOrderId_notFound() {
        when(paymentRepository.findByOrderId(anyString())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> paymentCommandService.getPaymentByOrderId("not-exist"));
        verify(paymentRepository).findByOrderId(anyString());
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
} 