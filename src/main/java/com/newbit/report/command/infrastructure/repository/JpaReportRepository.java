package com.newbit.report.command.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newbit.report.command.domain.aggregate.Report;
import com.newbit.report.command.domain.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

interface JpaReportJpaRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByPostId(Long postId);
}

@Repository
@RequiredArgsConstructor
public class JpaReportRepository implements ReportRepository {
    
    private final JpaReportJpaRepository jpaRepository;
    
    @Override
    public Report save(Report report) {
        return jpaRepository.save(report);
    }
    
    @Override
    public Report findById(Long id) {
        return jpaRepository.findById(id).orElse(null);
    }
    
    public List<Report> findAllByPostId(Long postId) {
        return jpaRepository.findAllByPostId(postId);
    }
}
