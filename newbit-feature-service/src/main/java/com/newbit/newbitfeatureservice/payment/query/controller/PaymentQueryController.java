package com.newbit.newbitfeatureservice.payment.query.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.payment.command.domain.aggregate.PaymentStatus;
import com.newbit.newbitfeatureservice.payment.controller.AbstractApiController;
import com.newbit.newbitfeatureservice.payment.query.dto.PaymentQueryDto;
import com.newbit.newbitfeatureservice.payment.query.service.PaymentQueryService;

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
@RequestMapping("/payments/query")
@RequiredArgsConstructor
@Tag(name = "결제 조회 API", description = "결제 내역 조회 관련 API")
public class PaymentQueryController extends AbstractApiController {

    private final PaymentQueryService paymentQueryService;

    @Operation(
        summary = "전체 결제 내역 조회", 
        description = "모든 결제 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentQueryDto>>> getAllPayments(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("전체 결제 내역 조회 요청: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(paymentQueryService.getAllPayments(pageable));
    }

    @Operation(
        summary = "사용자별 결제 내역 조회", 
        description = "특정 사용자의 모든 결제 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<PaymentQueryDto>>> getPaymentsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("사용자별 결제 내역 조회 요청: userId={}, page={}, size={}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(paymentQueryService.getPaymentsByUserId(userId, pageable));
    }

    @Operation(
        summary = "결제 상태별 내역 조회", 
        description = "특정 상태의 결제 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Page<PaymentQueryDto>>> getPaymentsByStatus(
            @Parameter(description = "결제 상태") @RequestParam PaymentStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("결제 상태별 내역 조회 요청: status={}, page={}, size={}", 
                status, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(paymentQueryService.getPaymentsByStatus(status, pageable));
    }

    @Operation(
        summary = "사용자별 결제 상태별 내역 조회", 
        description = "특정 사용자의 특정 상태 결제 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Page<PaymentQueryDto>>> getPaymentsByUserIdAndStatus(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "결제 상태 리스트") @RequestParam List<PaymentStatus> statuses,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("사용자별 결제 상태별 내역 조회 요청: userId={}, statuses={}, page={}, size={}", 
                userId, statuses, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(paymentQueryService.getPaymentsByUserIdAndStatus(userId, statuses, pageable));
    }

    @Operation(
        summary = "환불 내역이 있는 결제 조회", 
        description = "환불 내역이 있는 결제를 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/refunds")
    public ResponseEntity<ApiResponse<Page<PaymentQueryDto>>> getPaymentsWithRefunds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("환불 내역이 있는 결제 조회 요청: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(paymentQueryService.getPaymentsWithRefunds(pageable));
    }

    @Operation(
        summary = "사용자별 환불 내역이 있는 결제 조회", 
        description = "특정 사용자의 환불 내역이 있는 결제를 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/users/{userId}/refunds")
    public ResponseEntity<ApiResponse<Page<PaymentQueryDto>>> getPaymentsWithRefundsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("사용자별 환불 내역이 있는 결제 조회 요청: userId={}, page={}, size={}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(paymentQueryService.getPaymentsWithRefundsByUserId(userId, pageable));
    }

    @Operation(
        summary = "결제 상세 정보 조회", 
        description = "결제 ID로 결제 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = PaymentQueryDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보 없음")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentQueryDto>> getPaymentDetail(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId) {
        
        log.info("결제 상세 정보 조회 요청: paymentId={}", paymentId);
        return successResponse(paymentQueryService.getPaymentDetail(paymentId));
    }

    @Operation(
        summary = "주문번호 또는 결제키로 결제 정보 조회", 
        description = "주문번호 또는 결제키를 이용하여 결제 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = PaymentQueryDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보 없음")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaymentQueryDto>> searchPayment(
            @Parameter(description = "주문 번호") @RequestParam(required = false) String orderId,
            @Parameter(description = "결제 키") @RequestParam(required = false) String paymentKey) {
        
        log.info("주문번호 또는 결제키로 결제 정보 조회 요청: orderId={}, paymentKey={}", orderId, paymentKey);
        return successResponse(paymentQueryService.getPaymentByOrderIdOrPaymentKey(orderId, paymentKey));
    }
} 