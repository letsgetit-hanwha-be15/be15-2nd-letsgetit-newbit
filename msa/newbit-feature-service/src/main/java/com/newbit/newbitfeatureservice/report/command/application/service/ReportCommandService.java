package com.newbit.newbitfeatureservice.report.command.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.post.entity.Comment;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.newbitfeatureservice.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportType;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportRepository;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {

    private final ReportRepository reportRepository;
    private final ReportTypeRepository reportTypeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    private static final int REPORT_THRESHOLD = 50;

    public ReportCommandResponse createPostReport(ReportCreateRequest request) {
        if (request.getPostId() == null) {
            throw new IllegalArgumentException("게시글 ID는 필수입니다.");
        }
        
        ReportType reportType = reportTypeRepository.findById(request.getReportTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 유형입니다."));

        Report report = Report.createPostReport(
                request.getUserId(),
                request.getPostId(),
                reportType,
                request.getContent()
        );
        
        Report savedReport = reportRepository.save(report);
        
        incrementPostReportCount(request.getPostId());
        
        return new ReportCommandResponse(savedReport);
    }

    public ReportCommandResponse createCommentReport(ReportCreateRequest request) {
        if (request.getCommentId() == null) {
            throw new IllegalArgumentException("댓글 ID는 필수입니다.");
        }

        ReportType reportType = reportTypeRepository.findById(request.getReportTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 유형입니다."));

        Report report = Report.createCommentReport(
                request.getUserId(),
                request.getCommentId(),
                reportType,
                request.getContent()
        );
        
        Report savedReport = reportRepository.save(report);
        
        incrementCommentReportCount(request.getCommentId());
        
        return new ReportCommandResponse(savedReport);
    }
    
    private boolean incrementPostReportCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        
        post.setReportCount(post.getReportCount() + 1);
        
        if (post.getReportCount() >= REPORT_THRESHOLD) {
            post.softDelete();
            
            List<Report> reports = reportRepository.findAllByPostId(postId);
            for (Report report : reports) {
                report.updateStatus(ReportStatus.DELETED);
            }
            
            return true;
        }
        
        return false;
    }
    
    private boolean incrementCommentReportCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        
        comment.setReportCount(comment.getReportCount() + 1);
        
        if (comment.getReportCount() >= REPORT_THRESHOLD) {
            comment.softDelete();
            
            List<Report> reports = reportRepository.findAllByCommentId(commentId);
            for (Report report : reports) {
                report.updateStatus(ReportStatus.DELETED);
            }
            
            return true;
        }
        
        return false;
    }
    
    public ReportCommandResponse processReport(Long reportId, ReportStatus newStatus) {
        Report report = reportRepository.findById(reportId);
        if (report == null) {
            throw new IllegalArgumentException("존재하지 않는 신고입니다.");
        }
        
        report.updateStatus(newStatus);
        
        if (newStatus == ReportStatus.DELETED) {
            if (report.getPostId() != null) {
                Post post = postRepository.findById(report.getPostId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
                
                post.softDelete();
            } else if (report.getCommentId() != null) {
                Comment comment = commentRepository.findById(report.getCommentId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
                
                comment.softDelete();
            }
        }
        
        return new ReportCommandResponse(report);
    }
}
