package com.newbit.newbitfeatureservice.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentApproveRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentPrepareRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentApproveResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentPrepareResponse;
import com.newbit.newbitfeatureservice.payment.command.application.service.PaymentCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "결제 API", description = "결제 관련 API")
public class PaymentController extends AbstractApiController {

    private final PaymentCommandService paymentCommandService;

    @Operation(summary = "결제 준비", description = "결제에 필요한 정보를 준비하고 결제 위젯 URL을 반환합니다.")
    @PostMapping("/prepare")
    public ResponseEntity<ApiResponse<PaymentPrepareResponse>> preparePayment(
            @RequestBody PaymentPrepareRequest request) {
        PaymentPrepareResponse response = paymentCommandService.preparePayment(request);
        return successResponse(response);
    }

    @Operation(summary = "결제 승인", description = "결제 승인을 요청합니다.")
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<PaymentApproveResponse>> approvePayment(
            @RequestBody PaymentApproveRequest request) {
        PaymentApproveResponse response = paymentCommandService.approvePayment(request);
        return successResponse(response);
    }

    @Operation(summary = "결제 정보 조회", description = "결제 ID로 결제 정보를 조회합니다.")
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentApproveResponse>> getPayment(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId) {
        PaymentApproveResponse response = paymentCommandService.getPayment(paymentId);
        return successResponse(response);
    }

    @Operation(summary = "주문 ID로 결제 정보 조회", description = "주문 ID로 결제 정보를 조회합니다.")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentApproveResponse>> getPaymentByOrderId(
            @Parameter(description = "주문 ID") @PathVariable String orderId) {
        PaymentApproveResponse response = paymentCommandService.getPaymentByOrderId(orderId);
        return successResponse(response);
    }
    
    @Operation(summary = "결제 성공 콜백", description = "토스페이먼츠에서 결제 성공 시 호출되는 콜백 엔드포인트입니다.")
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<PaymentApproveResponse>> paymentSuccess(
            @Parameter(description = "결제 키") @RequestParam String paymentKey,
            @Parameter(description = "주문 ID") @RequestParam String orderId,
            @Parameter(description = "결제 금액") @RequestParam Long amount) {
        
        log.info("결제 성공 콜백: paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);
        
        PaymentApproveRequest approveRequest = PaymentApproveRequest.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .build();
        
        PaymentApproveResponse response = paymentCommandService.approvePayment(approveRequest);
        
        return successResponse(response);
    }
    
    @Operation(summary = "결제 실패 콜백", description = "토스페이먼츠에서 결제 실패 시 호출되는 콜백 엔드포인트입니다.")
    @GetMapping("/fail")
    public ResponseEntity<ApiResponse<String>> paymentFail(
            @Parameter(description = "에러 코드") @RequestParam String code,
            @Parameter(description = "에러 메시지") @RequestParam String message,
            @Parameter(description = "주문 ID") @RequestParam String orderId) {
        
        log.error("결제 실패 콜백: code={}, message={}, orderId={}", code, message, orderId);
        
        return successMessage("결제에 실패했습니다. 사유: " + message);
    }
} 