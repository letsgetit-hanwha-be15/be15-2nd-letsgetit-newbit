package com.newbit.report.command.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newbit.report.command.application.dto.request.ReportCreateRequest;
import com.newbit.report.command.application.dto.response.ReportCommandResponse;
import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {

    private final ReportRepository reportRepository;

    public ReportCommandResponse createPostReport(ReportCreateRequest request) {

        Report report = Report.createPostReport(
                request.getUserId(),
                request.getPostId(),
                request.getReportTypeId(),
                request.getContent()
        );
        
        Report savedReport = reportRepository.save(report);
        
        return new ReportCommandResponse(savedReport);
    }
}
