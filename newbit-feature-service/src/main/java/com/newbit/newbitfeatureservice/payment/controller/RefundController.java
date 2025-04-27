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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    
    @Operation(
        summary = "결제 전체 취소", 
        description = "결제를 전액 취소/환불합니다. 이미 취소된 결제나 취소 불가능한 상태의 결제는 취소할 수 없습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공", 
            content = @Content(schema = @Schema(implementation = PaymentRefundResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소 불가능한 결제"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보 없음")
    })
    @PostMapping("/payments/{paymentId}")
    public ResponseEntity<com.newbit.newbitfeatureservice.common.dto.ApiResponse<PaymentRefundResponse>> cancelPayment(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @Parameter(description = "취소 사유 및 옵션") @RequestBody PaymentCancelRequest request) {
        
        log.info("결제 전체 취소 요청: paymentId={}, reason={}", paymentId, request.getReason());
        PaymentRefundResponse response = refundCommandService.cancelPayment(paymentId, request.getReason());
        return successResponse(response);
    }
    
    @Operation(
        summary = "결제 부분 취소", 
        description = "결제를 부분 취소/환불합니다. 부분 취소는 부분 취소 가능한 결제에 대해서만 가능합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "부분 취소 성공", 
            content = @Content(schema = @Schema(implementation = PaymentRefundResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "부분 취소가 불가능한 결제"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보 없음")
    })
    @PostMapping("/payments/{paymentId}/partial")
    public ResponseEntity<com.newbit.newbitfeatureservice.common.dto.ApiResponse<PaymentRefundResponse>> cancelPaymentPartial(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @Parameter(description = "취소 금액, 취소 사유 등") @RequestBody PaymentCancelRequest request) {
        
        log.info("결제 부분 취소 요청: paymentId={}, amount={}, reason={}", 
                paymentId, request.getCancelAmount(), request.getReason());
        
        PaymentRefundResponse response = refundCommandService.cancelPaymentPartial(
                paymentId, 
                request.getCancelAmount(), 
                request.getReason()
        );
        return successResponse(response);
    }
    
    @Operation(
        summary = "가상계좌 결제 취소", 
        description = "가상계좌 결제를 취소하고 지정된 계좌로 환불합니다. 환불계좌 정보를 반드시 제공해야 합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가상계좌 취소 성공", 
            content = @Content(schema = @Schema(implementation = PaymentRefundResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "환불 계좌 정보 누락 또는 가상계좌 결제가 아님"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보 없음")
    })
    @PostMapping("/payments/{paymentId}/virtual")
    public ResponseEntity<com.newbit.newbitfeatureservice.common.dto.ApiResponse<PaymentRefundResponse>> cancelVirtualAccountPayment(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @Parameter(description = "환불 계좌 정보 및 취소 사유") @RequestBody PaymentCancelRequest request) {
        
        log.info("가상계좌 결제 취소 요청: paymentId={}, reason={}, 환불계좌정보={}", 
                paymentId, request.getReason(), 
                request.getRefundReceiveAccount() != null ? "제공됨" : "없음");
        
        PaymentRefundResponse response = refundCommandService.cancelVirtualAccountPayment(paymentId, request);
        return successResponse(response);
    }
    
    @Operation(
        summary = "결제 환불 내역 조회", 
        description = "특정 결제의 모든 환불 내역을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = List.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보 없음")
    })
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<com.newbit.newbitfeatureservice.common.dto.ApiResponse<List<PaymentRefundResponse>>> getRefundsByPaymentId(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId) {
        
        log.info("환불 내역 조회 요청: paymentId={}", paymentId);
        List<PaymentRefundResponse> response = refundCommandService.getRefundsByPaymentId(paymentId);
        return successResponse(response);
    }
    
    @Operation(
        summary = "환불 내역 상세 조회", 
        description = "특정 환불 내역을 상세 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = PaymentRefundResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환불 내역 없음")
    })
    @GetMapping("/{refundId}")
    public ResponseEntity<com.newbit.newbitfeatureservice.common.dto.ApiResponse<PaymentRefundResponse>> getRefund(
            @Parameter(description = "환불 ID") @PathVariable Long refundId) {
        
        log.info("환불 상세 조회 요청: refundId={}", refundId);
        PaymentRefundResponse response = refundCommandService.getRefund(refundId);
        return successResponse(response);
    }
} 