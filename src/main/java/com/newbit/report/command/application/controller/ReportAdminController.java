package com.newbit.report.command.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.common.dto.ApiResponse;
import com.newbit.report.command.application.dto.request.ReportUpdateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.application.service.ReportCommandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/report")
@RequiredArgsConstructor
public class ReportAdminController {

    private final ReportCommandService reportCommandService;
    

    @PatchMapping("/process")
    public ResponseEntity<ApiResponse<ReportCommandResponse>> processReport(@RequestBody @Valid ReportUpdateRequest request) {
        ReportCommandResponse response = reportCommandService.processReport(request.getReportId(), request.getStatus());
        ApiResponse<ReportCommandResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
} 