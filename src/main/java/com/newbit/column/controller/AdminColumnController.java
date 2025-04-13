package com.newbit.column.controller;

import com.newbit.auth.model.CustomUser;
import com.newbit.column.dto.request.ApproveColumnRequestDto;
import com.newbit.column.dto.request.RejectColumnRequestDto;
import com.newbit.column.dto.response.AdminColumnResponseDto;
import com.newbit.column.service.AdminColumnService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/columns")
public class AdminColumnController {

    private final AdminColumnService adminColumnService;

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
}
