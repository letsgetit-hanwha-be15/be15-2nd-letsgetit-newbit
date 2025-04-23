package com.newbit.newbitfeatureservice.report.query.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;
import com.newbit.newbitfeatureservice.report.query.service.ReportQueryService;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/reports")
@Tag(name = "신고 조회 API", description = "신고 관련 조회 기능 API")
public class ReportQueryController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportQueryController.class);
    
    private final ReportQueryService reportQueryService;
    
    public ReportQueryController(ReportQueryService reportQueryService) {
        this.reportQueryService = reportQueryService;
    }

    @Operation(summary = "신고 목록 조회", description = "신고 상태별 목록을 페이지 단위로 조회합니다.")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReports(
            @Parameter(description = "신고 상태") @RequestParam(required = false) ReportStatus status,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            logger.info("Fetching reports with status: {} and pageable: {}", status, pageable);
            Page<ReportDTO> reports = reportQueryService.findReports(status, pageable);
            logger.info("Successfully fetched reports: {}", reports);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            logger.error("Error fetching reports with status: {}", status, e);
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }

    @Operation(summary = "전체 신고 목록 조회", description = "모든 신고 목록을 페이징 없이 조회합니다.")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportDTO>>> getAllReports() {
        try {
            logger.info("Fetching all reports without paging");
            List<ReportDTO> reports = reportQueryService.findAllReportsWithoutPaging();
            logger.info("Successfully fetched all reports, count: {}", reports.size());
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            logger.error("Error fetching all reports", e);
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", "Error occurred: " + e.getMessage() + ", Class: " + e.getClass().getName()));
        }
    }
    
    @Operation(summary = "게시글 신고 목록 조회", description = "특정 게시글에 대한 신고 목록을 조회합니다.")
    @GetMapping("/post/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByPostId(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByPostId(postId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "댓글 신고 목록 조회", description = "특정 댓글에 대한 신고 목록을 조회합니다.")
    @GetMapping("/comment/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByCommentId(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByCommentId(commentId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "사용자별 신고 목록 조회", description = "특정 사용자가 신고한 목록을 조회합니다.")
    @GetMapping("/reporter/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByReporterId(
            @Parameter(description = "신고자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByReporterId(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "신고 유형별 목록 조회", description = "신고 유형별 목록을 조회합니다.")
    @GetMapping("/type/{reportTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByReportTypeId(
            @Parameter(description = "신고 유형 ID") @PathVariable Long reportTypeId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByReportTypeId(reportTypeId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "게시글 작성자별 신고 목록 조회", description = "특정 사용자가 작성한 게시글에 대한 신고 목록을 조회합니다.")
    @GetMapping("/post-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByPostUserId(
            @Parameter(description = "게시글 작성자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByPostUserId(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "댓글 작성자별 신고 목록 조회", description = "특정 사용자가 작성한 댓글에 대한 신고 목록을 조회합니다.")
    @GetMapping("/comment-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByCommentUserId(
            @Parameter(description = "댓글 작성자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByCommentUserId(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "컨텐츠 작성자별 신고 목록 조회", description = "특정 사용자가 작성한 모든 컨텐츠(게시글, 댓글)에 대한 신고 목록을 조회합니다.")
    @GetMapping("/content-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByContentUserId(
            @Parameter(description = "컨텐츠 작성자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports = reportQueryService.findReportsByContentUserId(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
    
    @Operation(summary = "상태 및 유형별 신고 목록 필터링", description = "신고 상태와 유형으로 필터링된 신고 목록을 조회합니다.")
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportDTO>>> getReportsByStatusAndType(
            @Parameter(description = "신고 상태") @RequestParam(required = false) ReportStatus status,
            @Parameter(description = "신고 유형 ID") @RequestParam(required = false) Long reportTypeId,
            @Parameter(description = "페이지 정보") @PageableDefault(size = 10) Pageable pageable) {
        
        try {
            Page<ReportDTO> reports;
            
            if (status != null && reportTypeId != null) {
                reports = reportQueryService.findReportsByStatusAndReportTypeId(status, reportTypeId, pageable);
            } else if (status != null) {
                reports = reportQueryService.findReports(status, pageable);
            } else if (reportTypeId != null) {
                reports = reportQueryService.findReportsByReportTypeId(reportTypeId, pageable);
            } else {
                reports = reportQueryService.findReports(null, pageable);
            }
            
            return ResponseEntity.ok(ApiResponse.success(reports));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("REPORT_FETCH_ERROR", e.getMessage()));
        }
    }
} 