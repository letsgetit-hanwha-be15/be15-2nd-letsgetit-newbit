package com.newbit.newbitfeatureservice.report.query.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;

public interface ReportJpaRepository extends JpaRepository<Report, Long> {

    // 상태별 조회를 위한 엔티티 반환 메소드
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    
    // 게시글 ID 기반 조회
    Page<Report> findByPostId(Long postId, Pageable pageable);
    
    // 댓글 ID 기반 조회
    Page<Report> findByCommentId(Long commentId, Pageable pageable);
    
    // 신고자 ID 기반 조회
    Page<Report> findByUserId(Long userId, Pageable pageable);

} 