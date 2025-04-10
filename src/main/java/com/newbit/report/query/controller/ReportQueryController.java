package com.newbit.report.query.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newbit.report.command.domain.aggregate.ReportStatus;
import com.newbit.report.query.service.ReportQueryService;
import com.newbit.report.query.dto.response.ReportDTO;

@RestController
@RequestMapping("/api/v1/report")
public class ReportQueryController {

    private final ReportQueryService reportQueryService;

    public ReportQueryController(ReportQueryService reportQueryService) {
        this.reportQueryService = reportQueryService;
    }


    @GetMapping("/reports")
    public ResponseEntity<Page<ReportDTO>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReports(status, pageable));
    }

    @GetMapping("/reports/all")
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        return ResponseEntity.ok(reportQueryService.findAllReportsWithoutPaging());
    }
    

    @GetMapping("/reports/post/{postId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByPostId(
            @PathVariable Long postId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByPostId(postId, pageable));
    }
    

    @GetMapping("/reports/comment/{commentId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByCommentId(
            @PathVariable Long commentId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByCommentId(commentId, pageable));
    }
    

    @GetMapping("/reports/reporter/{userId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByReporterId(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByReporterId(userId, pageable));
    }
    

    @GetMapping("/reports/type/{reportTypeId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByReportTypeId(
            @PathVariable Long reportTypeId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByReportTypeId(reportTypeId, pageable));
    }
    

    @GetMapping("/reports/post-user/{userId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByPostUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByPostUserId(userId, pageable));
    }
    

    @GetMapping("/reports/comment-user/{userId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByCommentUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByCommentUserId(userId, pageable));
    }
    

    @GetMapping("/reports/content-user/{userId}")
    public ResponseEntity<Page<ReportDTO>> getReportsByContentUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        return ResponseEntity.ok(reportQueryService.findReportsByContentUserId(userId, pageable));
    }
    

    @GetMapping("/reports/filter")
    public ResponseEntity<Page<ReportDTO>> getReportsByStatusAndType(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) Long reportTypeId,
            @PageableDefault(size = 10) Pageable pageable) {
        
        if (status != null && reportTypeId != null) {
            return ResponseEntity.ok(reportQueryService.findReportsByStatusAndReportTypeId(status, reportTypeId, pageable));
        } else if (status != null) {
            return ResponseEntity.ok(reportQueryService.findReports(status, pageable));
        } else if (reportTypeId != null) {
            return ResponseEntity.ok(reportQueryService.findReportsByReportTypeId(reportTypeId, pageable));
        } else {
            return ResponseEntity.ok(reportQueryService.findReports(null, pageable));
        }
    }
} 