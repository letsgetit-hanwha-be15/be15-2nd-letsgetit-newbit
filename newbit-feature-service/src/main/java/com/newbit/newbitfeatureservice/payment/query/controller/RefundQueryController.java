package com.newbit.newbitfeatureservice.payment.query.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.payment.controller.AbstractApiController;
import com.newbit.newbitfeatureservice.payment.query.dto.RefundQueryDto;
import com.newbit.newbitfeatureservice.payment.query.service.RefundQueryService;

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
@RequestMapping("/refunds/query")
@RequiredArgsConstructor
@Tag(name = "환불 조회 API", description = "환불 내역 조회 관련 API")
public class RefundQueryController extends AbstractApiController {

    private final RefundQueryService refundQueryService;

    @Operation(
        summary = "전체 환불 내역 조회", 
        description = "모든 환불 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RefundQueryDto>>> getAllRefunds(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("전체 환불 내역 조회 요청: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(refundQueryService.getAllRefunds(pageable));
    }

    @Operation(
        summary = "결제별 환불 내역 조회", 
        description = "특정 결제의 모든 환불 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<ApiResponse<Page<RefundQueryDto>>> getRefundsByPaymentId(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("결제별 환불 내역 조회 요청: paymentId={}, page={}, size={}", 
                paymentId, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(refundQueryService.getRefundsByPaymentId(paymentId, pageable));
    }

    @Operation(
        summary = "사용자별 환불 내역 조회", 
        description = "특정 사용자의 모든 환불 내역을 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Page<RefundQueryDto>>> getRefundsByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("사용자별 환불 내역 조회 요청: userId={}, page={}, size={}", 
                userId, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(refundQueryService.getRefundsByUserId(userId, pageable));
    }

    @Operation(
        summary = "부분 환불 내역 조회", 
        description = "특정 결제의 부분 환불 내역만 페이징하여 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/payments/{paymentId}/partial")
    public ResponseEntity<ApiResponse<Page<RefundQueryDto>>> getPartialRefundsByPaymentId(
            @Parameter(description = "결제 ID") @PathVariable Long paymentId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        
        log.info("부분 환불 내역 조회 요청: paymentId={}, page={}, size={}", 
                paymentId, pageable.getPageNumber(), pageable.getPageSize());
        return successResponse(refundQueryService.getPartialRefundsByPaymentId(paymentId, pageable));
    }

    @Operation(
        summary = "환불 상세 정보 조회", 
        description = "환불 ID로 환불 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", 
            content = @Content(schema = @Schema(implementation = RefundQueryDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환불 정보 없음")
    })
    @GetMapping("/{refundId}")
    public ResponseEntity<ApiResponse<RefundQueryDto>> getRefundDetail(
            @Parameter(description = "환불 ID") @PathVariable Long refundId) {
        
        log.info("환불 상세 정보 조회 요청: refundId={}", refundId);
        return successResponse(refundQueryService.getRefundDetail(refundId));
    }
} 