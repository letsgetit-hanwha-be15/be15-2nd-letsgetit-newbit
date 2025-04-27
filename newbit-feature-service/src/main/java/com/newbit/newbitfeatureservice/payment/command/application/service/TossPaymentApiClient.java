package com.newbit.newbitfeatureservice.payment.command.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto;
import com.newbit.newbitfeatureservice.payment.command.application.dto.TossPaymentApiDto.RefundReceiveAccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentApiClient {

    private final RestTemplate restTemplate;
    
    @Value("${payment.toss.api-key}")
    private String apiKey;
    
    @Value("${payment.toss.secret-key}")
    private String secretKey;
    
    @Value("${payment.toss.api-url}")
    private String apiUrl;
    
    @Value("${payment.toss.success-url}")
    private String successUrl;
    
    @Value("${payment.toss.fail-url}")
    private String failUrl;

    public String createPaymentWidgetUrl(Long amount, String orderId, String orderName) {
        TossPaymentApiDto.WidgetUrlRequest request = TossPaymentApiDto.WidgetUrlRequest.builder()
                .amount(amount)
                .orderId(orderId)
                .orderName(orderName)
                .successUrl(successUrl)
                .failUrl(failUrl)
                .build();
                
        return UriComponentsBuilder.fromUriString(apiUrl)
                .path("/v1/payments/widget/")
                .queryParam("amount", request.getAmount())
                .queryParam("orderId", request.getOrderId())
                .queryParam("orderName", request.getOrderName())
                .queryParam("successUrl", request.getSuccessUrl())
                .queryParam("failUrl", request.getFailUrl())
                .build()
                .toUriString();
    }

    public TossPaymentApiDto.PaymentResponse requestPaymentApproval(String paymentKey, String orderId, Long amount) {
        TossPaymentApiDto.PaymentConfirmRequest request = TossPaymentApiDto.PaymentConfirmRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
        
        HttpEntity<TossPaymentApiDto.PaymentConfirmRequest> entity = new HttpEntity<>(request, createAuthHeaders());
        
        return restTemplate.postForObject(
                apiUrl + "/v1/payments/confirm",
                entity,
                TossPaymentApiDto.PaymentResponse.class
        );
    }

    public TossPaymentApiDto.PaymentResponse getPaymentDetails(String paymentKey) {
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        
        return restTemplate.getForObject(
                apiUrl + "/v1/payments/" + paymentKey,
                TossPaymentApiDto.PaymentResponse.class,
                entity
        );
    }

    public TossPaymentApiDto.PaymentResponse cancelPayment(String paymentKey, String cancelReason) {
        TossPaymentApiDto.PaymentCancelRequest request = TossPaymentApiDto.PaymentCancelRequest.builder()
                .cancelReason(cancelReason)
                .build();
                
        return requestCancelPayment(paymentKey, request);
    }
    
    public TossPaymentApiDto.PaymentResponse cancelPaymentPartial(String paymentKey, String cancelReason, Long cancelAmount) {
        TossPaymentApiDto.PaymentCancelRequest request = TossPaymentApiDto.PaymentCancelRequest.builder()
                .cancelReason(cancelReason)
                .cancelAmount(cancelAmount)
                .build();
                
        return requestCancelPayment(paymentKey, request);
    }
    
    public TossPaymentApiDto.PaymentResponse cancelVirtualAccountPayment(
            String paymentKey, String cancelReason, Long cancelAmount, 
            String bankCode, String accountNumber, String holderName) {
        
        RefundReceiveAccount refundAccount = RefundReceiveAccount.builder()
                .bank(bankCode)
                .accountNumber(accountNumber)
                .holderName(holderName)
                .build();
        
        TossPaymentApiDto.PaymentCancelRequest request = TossPaymentApiDto.PaymentCancelRequest.builder()
                .cancelReason(cancelReason)
                .cancelAmount(cancelAmount)
                .refundReceiveAccount(refundAccount)
                .build();
                
        return requestCancelPayment(paymentKey, request);
    }
    
    private TossPaymentApiDto.PaymentResponse requestCancelPayment(String paymentKey, TossPaymentApiDto.PaymentCancelRequest request) {
        // 멱등성 키 생성
        String idempotencyKey = UUID.randomUUID().toString();
        
        HttpHeaders headers = createAuthHeaders();
        // 멱등성 키 헤더 추가
        headers.set("Idempotency-Key", idempotencyKey);
        
        HttpEntity<TossPaymentApiDto.PaymentCancelRequest> entity = new HttpEntity<>(request, headers);
        
        log.info("결제 취소 요청: paymentKey={}, cancelReason={}, idempotencyKey={}", 
                 paymentKey, request.getCancelReason(), idempotencyKey);
        
        return restTemplate.postForObject(
                apiUrl + "/v1/payments/" + paymentKey + "/cancel",
                entity,
                TossPaymentApiDto.PaymentResponse.class
        );
    }
    
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                (secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        return headers;
    }
} 