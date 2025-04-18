package com.newbit.settlement.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.common.dto.ApiResponse;
import com.newbit.settlement.dto.response.MentorSettlementDetailResponseDto;
import com.newbit.settlement.dto.response.MentorSettlementListResponseDto;
import com.newbit.settlement.service.MentorSettlementService;
import com.newbit.user.service.MentorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/settlements")
@Tag(name = "정산 API", description = "멘토 월별 정산 관련 API")
public class MentorSettlementController {

    private final MentorSettlementService mentorSettlementService;
    private final MentorService mentorService;

    @PostMapping("/generate")
    @Operation(
            summary = "멘토 월별 정산 생성",
            description = "특정 연도와 월을 기준으로 멘토 정산 내역을 생성하고, 관련된 판매내역을 정산 완료 상태로 변경합니다."
    )
    public ResponseEntity<Void> generateMonthlySettlement(
            @RequestParam int year,
            @RequestParam int month
    ) {
        mentorSettlementService.generateMonthlySettlements(year, month);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    @Operation(summary = "내 정산 내역 목록 조회", description = "로그인한 멘토의 정산 내역을 페이징 형태로 조회합니다.")
    public ApiResponse<MentorSettlementListResponseDto> getMySettlements(
            @AuthenticationPrincipal CustomUser customUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = customUser.getUserId();
        Long mentorId = mentorService.getMentorIdByUserId(userId);
        MentorSettlementListResponseDto response = mentorSettlementService.getMySettlements(mentorId, page, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/{settlementId}")
    @Operation(summary = "정산 상세 내역 조회", description = "멘토의 월별 정산 상세 정보를 조회합니다.")
    public ApiResponse<MentorSettlementDetailResponseDto> getSettlementDetail(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable Long settlementId
    ) {
        MentorSettlementDetailResponseDto response = mentorSettlementService.getSettlementDetail(settlementId);
        return ApiResponse.success(response);
    }

    @GetMapping("/my/{settlementId}/email")
    @Operation(summary = "정산 내역 이메일 발송", description = "해당 정산 ID에 대한 정산 내역을 이메일로 전송합니다.")
    public ApiResponse<Void> sendSettlementEmail(
            @AuthenticationPrincipal CustomUser customUser,
            @PathVariable Long settlementId
    ) {
        Long userId = customUser.getUserId();
        Long mentorId = mentorService.getMentorIdByUserId(userId);
        mentorSettlementService.sendSettlementEmail(mentorId, settlementId);
        return ApiResponse.success(null);
    }
}

