package com.newbit.newbitfeatureservice.report.command.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.newbitfeatureservice.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.newbitfeatureservice.report.command.application.service.ReportCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "신고 API", description = "신고 작성 관련 API")
public class ReportCommandController {

    private final ReportCommandService reportCommandService;

    @Operation(summary = "게시글 신고하기", description = "게시글을 신고합니다.")
    @PostMapping("/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReportCommandResponse>> createPostReport(
            @Parameter(description = "신고 정보", required = true) 
            @RequestBody @Valid ReportCreateRequest request) {
        ReportCommandResponse response = reportCommandService.createPostReport(request);
        ApiResponse<ReportCommandResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
    
    @Operation(summary = "댓글 신고하기", description = "댓글을 신고합니다.")
    @PostMapping("/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReportCommandResponse>> createCommentReport(
            @Parameter(description = "신고 정보", required = true) 
            @RequestBody @Valid ReportCreateRequest request) {
        ReportCommandResponse response = reportCommandService.createCommentReport(request);
        ApiResponse<ReportCommandResponse> apiResponse = ApiResponse.success(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
