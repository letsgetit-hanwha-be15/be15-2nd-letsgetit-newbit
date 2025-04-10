package com.newbit.report.command.domain.repository;

import java.util.Optional;

import com.newbit.report.command.domain.aggregate.ReportType;

public interface ReportTypeRepository {
    
    Optional<ReportType> findById(Long id);
    
} 