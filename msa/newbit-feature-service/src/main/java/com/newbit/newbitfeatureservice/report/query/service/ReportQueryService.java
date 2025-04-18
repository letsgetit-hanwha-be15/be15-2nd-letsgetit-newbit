package com.newbit.newbitfeatureservice.report.query.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.domain.repository.ReportQueryRepository;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;

@Service
public class ReportQueryService {

    private final ReportQueryRepository reportQueryRepository;

    public ReportQueryService(ReportQueryRepository reportQueryRepository) {
        this.reportQueryRepository = reportQueryRepository;
    }

    public Page<ReportDTO> findReports(ReportStatus status, Pageable pageable) {
        return reportQueryRepository.findReports(status, pageable);
    }

    public List<ReportDTO> findAllReportsWithoutPaging() {
        return reportQueryRepository.findAllWithoutPaging();
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