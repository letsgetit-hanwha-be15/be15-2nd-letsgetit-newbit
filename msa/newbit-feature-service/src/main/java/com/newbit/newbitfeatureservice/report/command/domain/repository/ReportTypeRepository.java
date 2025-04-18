package com.newbit.newbitfeatureservice.report.command.domain.repository;

import java.util.Optional;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportType;

public interface ReportTypeRepository {

    Optional<ReportType> findById(Long id);

    ReportType save(ReportType reportType);
} 