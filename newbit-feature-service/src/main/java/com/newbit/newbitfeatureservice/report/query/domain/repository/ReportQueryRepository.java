package com.newbit.newbitfeatureservice.report.query.domain.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;

public interface ReportQueryRepository {

    // 전체 조회
    List<ReportDTO> findAllWithoutPaging();

    // 신고 상태별 조회
    Page<ReportDTO> findReports(ReportStatus status, Pageable pageable);

    // 게시글 ID 기반 조회
    Page<ReportDTO> findReportsByPostId(Long postId, Pageable pageable);
    
    // 댓글 ID 기반 조회
    Page<ReportDTO> findReportsByCommentId(Long commentId, Pageable pageable);
    
    // 게시글 작성자 ID 기반 조회
    Page<ReportDTO> findReportsByPostUserId(Long userId, Pageable pageable);
    
    // 댓글 작성자 ID 기반 조회
    Page<ReportDTO> findReportsByCommentUserId(Long userId, Pageable pageable);
    
    // 신고자 ID 기반 조회
    Page<ReportDTO> findReportsByReporterId(Long userId, Pageable pageable);
    
    // 신고 유형별 조회
    Page<ReportDTO> findReportsByReportTypeId(Long reportTypeId, Pageable pageable);
    
    // 상태와 신고 유형별 조회
    Page<ReportDTO> findReportsByStatusAndReportTypeId(ReportStatus status, Long reportTypeId, Pageable pageable);
    
    // 게시글 또는 댓글 작성자 ID 기반 조회 (통합)
    Page<ReportDTO> findReportsByContentUserId(Long userId, Pageable pageable);

} 