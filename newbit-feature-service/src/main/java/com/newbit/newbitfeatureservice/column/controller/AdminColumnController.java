package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.column.dto.response.AdminColumnRequestResponseDto;
import com.newbit.newbitfeatureservice.column.dto.response.GetMyColumnRequestResponseDto;
import com.newbit.newbitfeatureservice.column.service.ColumnRequestService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.column.dto.request.ApproveColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.request.RejectColumnRequestDto;
import com.newbit.newbitfeatureservice.column.dto.response.AdminColumnResponseDto;
import com.newbit.newbitfeatureservice.column.service.AdminColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "칼럼 승인 API", description = "공개된 컬럼 조회 관련 API")
@RequestMapping("/columns")
public class AdminColumnController {

    private final AdminColumnService adminColumnService;
    private final ColumnRequestService columnRequestService;

    @PostMapping("/requests/approve/create")
    @Operation(summary = "칼럼 등록 요청 승인", description = "CREATE 타입의 칼럼 등록 요청을 승인합니다.")
    public AdminColumnResponseDto approveCreateColumn(
            @RequestBody ApproveColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
            ) {
        return adminColumnService.approveCreateColumnRequest(dto, customUser.getUserId());
    }

    @PostMapping("/requests/reject/create")
    @Operation(summary = "칼럼 등록 요청 거절", description = "CREATE 타입의 칼럼 등록 요청을 거절하고, 거절 사유를 기록합니다.")
    public AdminColumnResponseDto rejectCreateColumn(
            @RequestBody RejectColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return adminColumnService.rejectCreateColumnRequest(dto, customUser.getUserId());
    }

    @PostMapping("/requests/approve/update")
    @Operation(summary = "칼럼 수정 요청 승인", description = "UPDATE 타입의 칼럼 수정 요청을 승인합니다.")
    public AdminColumnResponseDto approveUpdateColumn(
            @RequestBody ApproveColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return adminColumnService.approveUpdateColumnRequest(dto, customUser.getUserId());
    }

    @PostMapping("/requests/reject/update")
    @Operation(summary = "칼럼 수정 요청 거절", description = "UPDATE 타입의 칼럼 수정 요청을 거절합니다.")
    public AdminColumnResponseDto rejectUpdateColumn(
            @RequestBody RejectColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return adminColumnService.rejectUpdateColumnRequest(dto, customUser.getUserId());
    }

    @PostMapping("/requests/approve/delete")
    @Operation(summary = "칼럼 삭제 요청 승인", description = "DELETE 타입의 칼럼 삭제 요청을 승인하고, 칼럼을 삭제 처리합니다.")
    public AdminColumnResponseDto approveDeleteColumn(
            @RequestBody ApproveColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return adminColumnService.approveDeleteColumnRequest(dto, customUser.getUserId());
    }

    @PostMapping("/requests/reject/delete")
    @Operation(summary = "칼럼 삭제 요청 거절", description = "DELETE 타입의 칼럼 삭제 요청을 거절하고, 거절 사유를 기록합니다.")
    public AdminColumnResponseDto rejectDeleteColumn(
            @RequestBody RejectColumnRequestDto dto,
            @AuthenticationPrincipal CustomUser customUser
    ) {
        return adminColumnService.rejectDeleteColumnRequest(dto, customUser.getUserId());
    }

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "모든 칼럼 요청 조회 (관리자용)", description = "관리자가 등록, 수정, 삭제 요청 목록을 전체 조회합니다.")
    public ApiResponse<Page<AdminColumnRequestResponseDto>> getAllColumnRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(columnRequestService.getAllColumnRequests(page, size));
    }

    @GetMapping("/requests/{requestId}")
    @Operation(summary = "칼럼 요청 상세 조회 (관리자용)", description = "요청 ID로 칼럼 요청 상세 정보를 조회합니다.")
    public ApiResponse<AdminColumnRequestResponseDto> getColumnRequestDetail(@PathVariable Long requestId) {
        return ApiResponse.success(columnRequestService.getAdminColumnRequestDetail(requestId));
    }

}
