package com.newbit.newbitfeatureservice.report.query.service;

import java.util.List;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.domain.repository.ReportQueryRepository;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;

@Service
public class ReportQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ReportQueryService.class);
    
    private final ReportQueryRepository reportQueryRepository;

    public ReportQueryService(ReportQueryRepository reportQueryRepository) {
        this.reportQueryRepository = reportQueryRepository;
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
} 