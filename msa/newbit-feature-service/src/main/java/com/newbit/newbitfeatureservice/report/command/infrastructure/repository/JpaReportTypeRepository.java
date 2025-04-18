package com.newbit.newbitfeatureservice.report.command.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportType;
import com.newbit.newbitfeatureservice.report.command.domain.repository.ReportTypeRepository;

import lombok.RequiredArgsConstructor;


interface ReportTypeJpaRepository extends JpaRepository<ReportType, Long> {
}

@Repository
@RequiredArgsConstructor
public class JpaReportTypeRepository implements ReportTypeRepository {
    
    private final ReportTypeJpaRepository jpaRepository;
    
    @Override
    public Optional<ReportType> findById(Long id) {
        return jpaRepository.findById(id);
    }
    
    @Override
    public ReportType save(ReportType reportType) {
        return jpaRepository.save(reportType);
    }
} 