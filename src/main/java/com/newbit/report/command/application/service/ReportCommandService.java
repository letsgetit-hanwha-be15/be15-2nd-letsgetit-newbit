package com.newbit.report.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.domain.aggregate.Report;
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
        
        return new ReportCommandResponse(savedReport);
    }
}
