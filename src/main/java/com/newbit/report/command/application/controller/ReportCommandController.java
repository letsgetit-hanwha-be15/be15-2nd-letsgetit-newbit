package com.newbit.report.command.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.common.dto.ApiResponse;
import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.application.service.ReportCommandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportCommandController {

    private final ReportCommandService reportCommandService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<ReportCommandResponse>> createPostReport(@RequestBody @Valid ReportCreateRequest request) {
        ReportCommandResponse response = reportCommandService.createPostReport(request);
        ApiResponse<ReportCommandResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
