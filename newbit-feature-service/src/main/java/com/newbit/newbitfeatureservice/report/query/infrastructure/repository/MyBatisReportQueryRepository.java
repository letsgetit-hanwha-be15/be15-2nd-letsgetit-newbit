package com.newbit.newbitfeatureservice.report.query.infrastructure.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.domain.repository.ReportQueryRepository;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;
import com.newbit.newbitfeatureservice.report.query.infrastructure.mapper.ReportMapper;

@Repository
@Primary
public class MyBatisReportQueryRepository implements ReportQueryRepository {

    private final ReportMapper reportMapper;

    public MyBatisReportQueryRepository(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<ReportDTO> findAllWithoutPaging() {
        return reportMapper.findAllWithoutPaging();
    }

    @Override
    public Page<ReportDTO> findReports(ReportStatus status, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByPostId(Long postId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("postId", postId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByCommentId(Long commentId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("commentId", commentId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByReporterId(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByReportTypeId(Long reportTypeId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("reportTypeId", reportTypeId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByStatusAndReportTypeId(ReportStatus status, Long reportTypeId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("reportTypeId", reportTypeId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByPostUserId(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("postUserId", userId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByCommentUserId(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("commentUserId", userId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }

    @Override
    public Page<ReportDTO> findReportsByContentUserId(Long userId, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        params.put("contentUserId", userId);
        params.put("offset", pageable.getOffset());
        params.put("pageSize", pageable.getPageSize());
        
        List<ReportDTO> reports = reportMapper.findReports(params);
        int total = reportMapper.countReports(params);
        
        return new PageImpl<>(reports, pageable, total);
    }
} 