package com.newbit.report.command.domain.repository;

import com.newbit.report.command.domain.aggregate.Report;

public interface ReportRepository {
    
    Report save(Report report);
    
    Report findById(Long id);
}
