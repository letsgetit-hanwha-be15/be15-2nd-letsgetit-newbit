package com.newbit.payment.command.application.service;

import com.newbit.payment.command.application.dto.TossPaymentApiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

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
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                (secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        
        HttpEntity<TossPaymentApiDto.PaymentConfirmRequest> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(
                apiUrl + "/v1/payments/confirm",
                entity,
                TossPaymentApiDto.PaymentResponse.class
        );
    }

    public TossPaymentApiDto.PaymentResponse getPaymentDetails(String paymentKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                (secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        return restTemplate.getForObject(
                apiUrl + "/v1/payments/" + paymentKey,
                TossPaymentApiDto.PaymentResponse.class,
                entity
        );
    }
} 