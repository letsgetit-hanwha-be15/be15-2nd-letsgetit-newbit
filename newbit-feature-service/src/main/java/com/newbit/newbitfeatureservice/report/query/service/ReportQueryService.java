package com.newbit.newbitfeatureservice.report.query.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.service.CommentService;
import com.newbit.newbitfeatureservice.post.service.PostService;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.domain.repository.ReportQueryRepository;
import com.newbit.newbitfeatureservice.report.query.dto.ReportedCommentResponse;
import com.newbit.newbitfeatureservice.report.query.dto.ReportedPostResponse;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;

@Service
@Transactional(readOnly = true)
public class ReportQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ReportQueryService.class);
    
    private final ReportQueryRepository reportQueryRepository;
    private final PostService postService;
    private final CommentService commentService;

    public ReportQueryService(ReportQueryRepository reportQueryRepository, PostService postService, CommentService commentService) {
        this.reportQueryRepository = reportQueryRepository;
        this.postService = postService;
        this.commentService = commentService;
    }

    public Page<ReportDTO> findReports(ReportStatus status, Pageable pageable) {
        try {
            return reportQueryRepository.findReports(status, pageable);
        } catch (Exception e) {
            logger.error("Error finding reports with status {}: {}", status, e.getMessage(), e);
            throw new BusinessException(ErrorCode.REPORT_PROCESS_ERROR);
        }
    }

    public List<ReportDTO> findAllReportsWithoutPaging() {
        try {
            logger.info("Finding all reports without paging");
            List<ReportDTO> reports = reportQueryRepository.findAllWithoutPaging();
            logger.info("Found {} reports", reports.size());
            return reports;
        } catch (Exception e) {
            logger.error("Error finding all reports: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.REPORT_PROCESS_ERROR);
        }
    }

    public Page<ReportDTO> findReportsByPostId(Long postId, Pageable pageable) {
        return reportQueryRepository.findReportsByPostId(postId, pageable);
    }

    public Page<ReportDTO> findReportsByCommentId(Long commentId, Pageable pageable) {
        return reportQueryRepository.findReportsByCommentId(commentId, pageable);
    }

    public Page<ReportDTO> findReportsByReporterId(Long userId, Pageable pageable) {
        return reportQueryRepository.findReportsByReporterId(userId, pageable);
    }

    public Page<ReportDTO> findReportsByReportTypeId(Long reportTypeId, Pageable pageable) {
        return reportQueryRepository.findReportsByReportTypeId(reportTypeId, pageable);
    }

    public Page<ReportDTO> findReportsByStatusAndReportTypeId(ReportStatus status, Long reportTypeId, Pageable pageable) {
        return reportQueryRepository.findReportsByStatusAndReportTypeId(status, reportTypeId, pageable);
    }

    public Page<ReportDTO> findReportsByPostUserId(Long userId, Pageable pageable) {
        return reportQueryRepository.findReportsByPostUserId(userId, pageable);
    }

    public Page<ReportDTO> findReportsByCommentUserId(Long userId, Pageable pageable) {
        return reportQueryRepository.findReportsByCommentUserId(userId, pageable);
    }

    public Page<ReportDTO> findReportsByContentUserId(Long userId, Pageable pageable) {
        return reportQueryRepository.findReportsByContentUserId(userId, pageable);
    }

    public List<ReportedPostResponse> findReportedPosts() {
        List<ReportDTO> allReports = reportQueryRepository.findAllWithoutPaging();
        List<ReportDTO> postReports = allReports.stream()
                                                .filter(report -> report.getPostId() != null)
                                                .toList();

        Map<Long, List<ReportDTO>> reportsByPostId = postReports.stream()
            .collect(Collectors.groupingBy(ReportDTO::getPostId));

        return reportsByPostId.entrySet().stream()
            .map(entry -> {
                Long postId = entry.getKey();
                List<ReportDTO> reports = entry.getValue();
                
                String lastReportContent = reports.stream()
                    .max(Comparator.comparing(ReportDTO::getCreatedAt))
                    .map(ReportDTO::getContent)
                    .orElse("N/A");

                String postTitle;
                try {
                    postTitle = postService.getPostTitle(postId);
                } catch (BusinessException e) {
                    logger.warn("Could not find post title for postId {}: {}", postId, e.getMessage());
                    postTitle = "Post Not Found";
                }

                return ReportedPostResponse.builder()
                    .postId(postId)
                    .postTitle(postTitle)
                    .reportCount((long) reports.size())
                    .lastReportContent(lastReportContent)
                    .build();
            })
            .sorted(Comparator.comparing(ReportedPostResponse::getReportCount).reversed())
            .collect(Collectors.toList());
    }

    public List<ReportedCommentResponse> findReportedComments() {
        List<ReportDTO> allReports = reportQueryRepository.findAllWithoutPaging();
        List<ReportDTO> commentReports = allReports.stream()
                                                   .filter(report -> report.getCommentId() != null)
                                                   .toList();

        Map<Long, List<ReportDTO>> reportsByCommentId = commentReports.stream()
            .collect(Collectors.groupingBy(ReportDTO::getCommentId));

        return reportsByCommentId.entrySet().stream()
            .map(entry -> {
                Long commentId = entry.getKey();
                List<ReportDTO> reports = entry.getValue();

                String lastReportContent = reports.stream()
                    .max(Comparator.comparing(ReportDTO::getCreatedAt))
                    .map(ReportDTO::getContent)
                    .orElse("N/A");

                String commentContent;
                try {
                    commentContent = commentService.getCommentContent(commentId);
                } catch (BusinessException e) {
                    logger.warn("Could not find comment content for commentId {}: {}", commentId, e.getMessage());
                    commentContent = "Comment Not Found";
                } catch (Exception e) {
                    logger.error("Error fetching comment content for commentId {}: {}", commentId, e.getMessage(), e);
                    commentContent = "Error Fetching Comment";
                }

                return ReportedCommentResponse.builder()
                    .commentId(commentId)
                    .commentContent(commentContent)
                    .reportCount((long) reports.size())
                    .lastReportContent(lastReportContent)
                    .build();
            })
            .sorted(Comparator.comparing(ReportedCommentResponse::getReportCount).reversed())
            .collect(Collectors.toList());
    }
} 