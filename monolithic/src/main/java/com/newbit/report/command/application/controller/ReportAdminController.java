package com.newbit.report.command.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.common.dto.ApiResponse;
import com.newbit.report.command.application.dto.request.ReportUpdateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.application.service.ReportCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "신고 관리자 API", description = "신고 관리자 관련 API")
public class ReportAdminController {

    private final ReportCommandService reportCommandService;
    
    @Operation(summary = "신고 처리하기", description = "관리자가 신고를 처리합니다. 상태를 변경합니다.")
    @PatchMapping("/process")
    public ResponseEntity<ApiResponse<ReportCommandResponse>> processReport(
            @Parameter(description = "신고 처리 정보", required = true)
            @RequestBody @Valid ReportUpdateRequest request) {
        try {
            ReportCommandResponse response = reportCommandService.processReport(request.reportId(), request.status());
            ApiResponse<ReportCommandResponse> apiResponse = ApiResponse.success(response);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            ApiResponse<ReportCommandResponse> apiResponse = ApiResponse.failure("REPORT_PROCESS_ERROR", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }
} 