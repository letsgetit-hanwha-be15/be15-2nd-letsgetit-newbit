package com.newbit.report.command.domain.repository;

import java.util.List;
import java.util.Optional;

import com.newbit.report.command.domain.aggregate.Report;

public interface ReportRepository {
    
    Report save(Report report);
    
    Optional<Report> findById(Long id);
    
    List<Report> findAllByPostId(Long postId);
    
    List<Report> findAllByCommentId(Long commentId);
}
