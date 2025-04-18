package com.newbit.newbitfeatureservice.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.payment.command.application.dto.request.PaymentCancelRequest;
import com.newbit.newbitfeatureservice.payment.command.application.dto.response.PaymentRefundResponse;
import com.newbit.newbitfeatureservice.payment.command.application.service.RefundCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
@Tag(name = "결제 취소/환불 API", description = "결제 취소/환불 관련 API")
public class RefundController extends AbstractApiController {

    private final RefundCommandService refundCommandService;
    
    @Operation(summary = "결제 전체 취소", description = "결제를 전액 취소/환불합니다.")
    @PostMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentRefundResponse>> cancelPayment(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @RequestBody PaymentCancelRequest request) {
        
        log.info("결제 전체 취소 요청: paymentId={}, reason={}", paymentId, request.getReason());
        PaymentRefundResponse response = refundCommandService.cancelPayment(paymentId, request.getReason());
        return successResponse(response);
    }
    
    @Operation(summary = "결제 부분 취소", description = "결제를 부분 취소/환불합니다.")
    @PostMapping("/payments/{paymentId}/partial")
    public ResponseEntity<ApiResponse<PaymentRefundResponse>> cancelPaymentPartial(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @RequestBody PaymentCancelRequest request) {
        
        log.info("결제 부분 취소 요청: paymentId={}, amount={}, reason={}", 
                paymentId, request.getCancelAmount(), request.getReason());
        
        PaymentRefundResponse response = refundCommandService.cancelPaymentPartial(
                paymentId, 
                request.getCancelAmount(), 
                request.getReason()
        );
        return successResponse(response);
    }
    
    @Operation(summary = "결제 환불 내역 조회", description = "결제의 환불 내역을 조회합니다.")
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<List<PaymentRefundResponse>>> getRefundsByPaymentId(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId) {
        
        log.info("환불 내역 조회 요청: paymentId={}", paymentId);
        List<PaymentRefundResponse> response = refundCommandService.getRefundsByPaymentId(paymentId);
        return successResponse(response);
    }
    
    @Operation(summary = "환불 내역 상세 조회", description = "특정 환불 내역을 상세 조회합니다.")
    @GetMapping("/{refundId}")
    public ResponseEntity<ApiResponse<PaymentRefundResponse>> getRefund(
            @Parameter(description = "환불 ID") @PathVariable Long refundId) {
        
        log.info("환불 상세 조회 요청: refundId={}", refundId);
        PaymentRefundResponse response = refundCommandService.getRefund(refundId);
        return successResponse(response);
    }
} 