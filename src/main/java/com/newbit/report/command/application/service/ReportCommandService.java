package com.newbit.report.command.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.post.service.CommentService;
import com.newbit.post.service.PostService;
import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.aggregate.ReportStatus;
import com.newbit.report.command.domain.aggregate.ReportType;
import com.newbit.report.command.domain.repository.ReportRepository;
import com.newbit.report.command.domain.repository.ReportTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {

    private final ReportRepository reportRepository;
    private final ReportTypeRepository reportTypeRepository;
    private final PostService postService;
    private final CommentService commentService;

    private static final int REPORT_THRESHOLD = 50;

    public ReportCommandResponse createPostReport(ReportCreateRequest request) {
        if (request.postId() == null) {
            throw new BusinessException(ErrorCode.REPORT_CONTENT_NOT_FOUND);
        }
        
        ReportType reportType = reportTypeRepository.findById(request.reportTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_TYPE_NOT_FOUND));

        Report report = Report.createPostReport(
                request.userId(),
                request.postId(),
                reportType,
                request.content()
        );
        
        Report savedReport = reportRepository.save(report);
        
        incrementPostReportCount(request.postId());
        
        return new ReportCommandResponse(savedReport);
    }

    public ReportCommandResponse createCommentReport(ReportCreateRequest request) {
        if (request.commentId() == null) {
            throw new BusinessException(ErrorCode.REPORT_CONTENT_NOT_FOUND);
        }

        ReportType reportType = reportTypeRepository.findById(request.reportTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_TYPE_NOT_FOUND));

        Report report = Report.createCommentReport(
                request.userId(),
                request.commentId(),
                reportType,
                request.content()
        );
        
        Report savedReport = reportRepository.save(report);
        
        incrementCommentReportCount(request.commentId());
        
        return new ReportCommandResponse(savedReport);
    }
    
    private boolean incrementPostReportCount(Long postId) {
        postService.increaseReportCount(postId);
        
        int reportCount = postService.getReportCountByPostId(postId);
        if (reportCount >= REPORT_THRESHOLD) {
            postService.deletePostAsAdmin(postId);
            
            List<Report> reports = reportRepository.findAllByPostId(postId);
            for (Report report : reports) {
                report.updateStatus(ReportStatus.DELETED);
            }
            
            return true;
        }
        
        return false;
    }
    
    private boolean incrementCommentReportCount(Long commentId) {
        commentService.increaseReportCount(commentId);
        
        int reportCount = commentService.getReportCount(commentId);
        if (reportCount >= REPORT_THRESHOLD) {
            commentService.deleteCommentAsAdmin(commentId);
            
            List<Report> reports = reportRepository.findAllByCommentId(commentId);
            for (Report report : reports) {
                report.updateStatus(ReportStatus.DELETED);
            }
            
            return true;
        }
        
        return false;
    }
    
    public ReportCommandResponse processReport(Long reportId, ReportStatus newStatus) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));
        
        report.updateStatus(newStatus);
        
        if (newStatus == ReportStatus.DELETED) {
            if (report.getPostId() != null) {
                postService.deletePostAsAdmin(report.getPostId());
            } else if (report.getCommentId() != null) {
                commentService.deleteCommentAsAdmin(report.getCommentId());
            }
        }
        
        return new ReportCommandResponse(report);
    }
}
