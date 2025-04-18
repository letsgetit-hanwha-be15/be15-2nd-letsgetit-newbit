package com.newbit.newbitfeatureservice.report.command.domain.repository;

import java.util.List;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;

public interface ReportRepository {
    
    Report save(Report report);
    
    Report findById(Long id);
    
    List<Report> findAllByPostId(Long postId);
    
    List<Report> findAllByCommentId(Long commentId);
}
