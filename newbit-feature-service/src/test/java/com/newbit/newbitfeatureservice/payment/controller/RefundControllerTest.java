package com.newbit.newbitfeatureservice.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentCancelRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentRefundResponse;
import com.newbit.newbitfeatureservice.payment.command.application.service.RefundCommandService;

@WebMvcTest(controllers = RefundController.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {RefundController.class, RefundControllerTest.TestSecurityConfig.class})
public class RefundControllerTest {

    // Security Configuration for tests
    public static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RefundCommandService refundCommandService;

    private PaymentRefundResponse refundResponse;
    private List<PaymentRefundResponse> refundResponseList;
    
    @BeforeEach
    void setUp() {
        // Create a mock response using Mockito
        refundResponse = Mockito.mock(PaymentRefundResponse.class);
        when(refundResponse.getRefundId()).thenReturn(1L);
        when(refundResponse.getPaymentId()).thenReturn(100L);
        when(refundResponse.getAmount()).thenReturn(BigDecimal.valueOf(10000));
        when(refundResponse.getReason()).thenReturn("테스트 취소 사유");
        when(refundResponse.getRefundKey()).thenReturn("test-refund-key");
        when(refundResponse.getRefundedAt()).thenReturn(LocalDateTime.now());
        
        refundResponseList = Arrays.asList(refundResponse);
    }

    @Test
    @DisplayName("결제 전체 취소 테스트")
    void cancelPaymentTest() throws Exception {
        // Given
        when(refundCommandService.cancelPayment(eq(100L), anyString()))
            .thenReturn(refundResponse);
        
        // Create a proper request with all required fields
        PaymentCancelRequest request = PaymentCancelRequest.builder()
            .reason("테스트 취소 사유")
            .build();
        
        // When & Then
        mockMvc.perform(post("/refunds/payments/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.refundId").value(1L))
                .andExpect(jsonPath("$.data.paymentId").value(100L));
    }

    @Test
    @DisplayName("결제 부분 취소 테스트")
    void cancelPaymentPartialTest() throws Exception {
        // Given
        when(refundCommandService.cancelPaymentPartial(
                eq(100L), 
                any(BigDecimal.class), 
                anyString()))
            .thenReturn(refundResponse);
        
        // Create a proper request with all required fields
        PaymentCancelRequest request = PaymentCancelRequest.builder()
            .reason("부분 취소 테스트")
            .cancelAmount(BigDecimal.valueOf(5000))
            .build();
        
        // When & Then
        mockMvc.perform(post("/refunds/payments/100/partial")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.refundId").value(1L));
    }

    @Test
    @DisplayName("가상계좌 결제 취소 테스트")
    void cancelVirtualAccountPaymentTest() throws Exception {
        // Given
        when(refundCommandService.cancelVirtualAccountPayment(
                eq(100L), 
                any(PaymentCancelRequest.class)))
            .thenReturn(refundResponse);
        
        // Create a proper request with all required fields including refund account info
        PaymentCancelRequest.RefundReceiveAccount account = PaymentCancelRequest.RefundReceiveAccount.builder()
            .bank("088")
            .accountNumber("123456789")
            .holderName("홍길동")
            .build();
            
        PaymentCancelRequest request = PaymentCancelRequest.builder()
            .reason("가상계좌 취소 테스트")
            .refundReceiveAccount(account)
            .build();
        
        // When & Then
        mockMvc.perform(post("/refunds/payments/100/virtual")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.refundId").value(1L));
    }

    @Test
    @DisplayName("결제별 환불 내역 조회 테스트")
    void getRefundsByPaymentIdTest() throws Exception {
        // Given
        when(refundCommandService.getRefundsByPaymentId(eq(100L)))
            .thenReturn(refundResponseList);

        // When & Then
        mockMvc.perform(get("/refunds/payments/100"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].refundId").value(1L));
    }

    @Test
    @DisplayName("환불 상세 조회 테스트")
    void getRefundTest() throws Exception {
        // Given
        when(refundCommandService.getRefund(eq(1L)))
            .thenReturn(refundResponse);

        // When & Then
        mockMvc.perform(get("/refunds/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.refundId").value(1L));
    }
} 