package com.newbit.payment.command.application.service;

import com.newbit.payment.command.application.dto.TossPaymentApiDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TossPaymentApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TossPaymentApiClient tossPaymentApiClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tossPaymentApiClient, "apiUrl", "https://api.tosspayments.com");
        ReflectionTestUtils.setField(tossPaymentApiClient, "apiKey", "test_api_key");
        ReflectionTestUtils.setField(tossPaymentApiClient, "secretKey", "test_secret_key");
        ReflectionTestUtils.setField(tossPaymentApiClient, "successUrl", "https://example.com/success");
        ReflectionTestUtils.setField(tossPaymentApiClient, "failUrl", "https://example.com/fail");
    }

    @Test
    @DisplayName("결제 위젯 URL 생성 테스트")
    void createPaymentWidgetUrl() {
        // when
        String url = tossPaymentApiClient.createPaymentWidgetUrl(10000L, "test-order-id", "테스트 상품");

        // then
        assertTrue(url.contains("https://api.tosspayments.com/v1/payments/widget/"));
        assertTrue(url.contains("amount=10000"));
        assertTrue(url.contains("orderId=test-order-id"));
        assertTrue(url.contains("orderName=테스트 상품"));
        assertTrue(url.contains("successUrl=https://example.com/success"));
        assertTrue(url.contains("failUrl=https://example.com/fail"));
    }

    @Test
    @DisplayName("결제 승인 요청 테스트")
    void requestPaymentApproval() {
        // given
        TossPaymentApiDto.PaymentResponse mockResponse = createMockPaymentResponse();
        
        when(restTemplate.postForObject(
                eq("https://api.tosspayments.com/v1/payments/confirm"),
                any(HttpEntity.class),
                eq(TossPaymentApiDto.PaymentResponse.class)
        )).thenReturn(mockResponse);

        // when
        TossPaymentApiDto.PaymentResponse response = tossPaymentApiClient.requestPaymentApproval(
                "test-payment-key", 
                "test-order-id", 
                10000L
        );

        // then
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals("DONE", response.getStatus());
    }

    @Test
    @DisplayName("결제 조회 테스트")
    void getPaymentDetails() {
        // given
        TossPaymentApiDto.PaymentResponse mockResponse = createMockPaymentResponse();
        
        when(restTemplate.getForObject(
                eq("https://api.tosspayments.com/v1/payments/test-payment-key"),
                eq(TossPaymentApiDto.PaymentResponse.class),
                any(HttpEntity.class)
        )).thenReturn(mockResponse);

        // when
        TossPaymentApiDto.PaymentResponse response = tossPaymentApiClient.getPaymentDetails("test-payment-key");

        // then
        assertNotNull(response);
        assertEquals("test-payment-key", response.getPaymentKey());
        assertEquals("test-order-id", response.getOrderId());
        assertEquals("DONE", response.getStatus());
    }

    private TossPaymentApiDto.PaymentResponse createMockPaymentResponse() {
        Map<String, Object> receiptMap = new HashMap<>();
        receiptMap.put("url", "https://receipt.url");
        
        return TossPaymentApiDto.PaymentResponse.builder()
                .paymentKey("test-payment-key")
                .orderId("test-order-id")
                .orderName("테스트 상품")
                .totalAmount(10000L)
                .status("DONE")
                .approvedAt("2023-01-01T12:00:00")
                .receipt(receiptMap)
                .build();
    }
} 