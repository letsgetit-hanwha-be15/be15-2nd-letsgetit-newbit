package com.newbit.report.command.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newbit.report.command.domain.aggregate.ReportType;
import com.newbit.report.command.domain.repository.ReportTypeRepository;

import lombok.RequiredArgsConstructor;

interface JpaReportTypeJpaRepository extends JpaRepository<ReportType, Long> {
}

@Repository
@RequiredArgsConstructor
public class JpaReportTypeRepository implements ReportTypeRepository {
    
    private final JpaReportTypeJpaRepository jpaRepository;
    
    @Override
    public Optional<ReportType> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
} 