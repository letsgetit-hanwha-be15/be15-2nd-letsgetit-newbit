package com.newbit.newbitfeatureservice.payment.command.application.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 주의: 이 테스트는 실제 Toss 페이먼츠 API와 통신합니다.
 * 테스트 API 키를 사용하고 있으나, 과도한 API 호출이 발생할 수 있으므로 
 * 필요한 경우에만 실행하세요.
 */
@Disabled
@SpringBootTest
@ActiveProfiles("test")
@Tag("LiveApiTest")
public class TossPaymentApiLiveTest {

    @Autowired
    private TossPaymentApiClient tossPaymentApiClient;
    
    @Value("${payment.toss.api-key}")
    private String apiKey;
    
    private String testOrderId;
    private final Long testAmount = 15000L;
    private final String testOrderName = "통합테스트 상품";

    @BeforeEach
    void setUp() {
        this.testOrderId = "test-" + UUID.randomUUID().toString();
    }

    @Test
    @DisplayName("결제 위젯 URL 생성 API 호출 테스트")
    void createPaymentWidgetUrl_live() {
        // when
        String widgetUrl = tossPaymentApiClient.createPaymentWidgetUrl(
                testAmount, 
                testOrderId, 
                testOrderName
        );
        
        // then
        assertNotNull(widgetUrl);
        assertTrue(widgetUrl.contains("https://api.tosspayments.com/v1/payments/widget/"));
        assertTrue(widgetUrl.contains("amount=" + testAmount));
        assertTrue(widgetUrl.contains("orderId=" + testOrderId));
        System.out.println("결제 위젯 URL: " + widgetUrl);
    }
    
    @Test
    @DisplayName("결제 취소 API 호출 테스트 - 존재하지 않는 키 예외 처리 테스트")
    void cancelPayment_liveWithExceptionExpected() {
        // given
        String dummyPaymentKey = "dummy_payment_key_" + UUID.randomUUID();
        String cancelReason = "통합 테스트 취소";
        
        // when, then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            tossPaymentApiClient.cancelPayment(dummyPaymentKey, cancelReason);
        });
        
        // 존재하지 않는 키 또는 권한 오류로 404 또는 401 예상
        assertTrue(exception.getStatusCode().is4xxClientError());
        System.out.println("예상된 에러 발생: " + exception.getStatusCode() + " - " + exception.getMessage());
    }
    
    @Test
    @DisplayName("결제 조회 API 호출 테스트 - 존재하지 않는 키 예외 처리 테스트")
    void getPaymentDetails_liveWithExceptionExpected() {
        // given
        String dummyPaymentKey = "dummy_payment_key_" + UUID.randomUUID();
        
        // when, then
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            tossPaymentApiClient.getPaymentDetails(dummyPaymentKey);
        });
        
        // 존재하지 않는 키 또는 권한 오류로 404 또는 401 예상  
        assertTrue(exception.getStatusCode().is4xxClientError());
        System.out.println("예상된 에러 발생: " + exception.getStatusCode() + " - " + exception.getMessage());
    }
} 