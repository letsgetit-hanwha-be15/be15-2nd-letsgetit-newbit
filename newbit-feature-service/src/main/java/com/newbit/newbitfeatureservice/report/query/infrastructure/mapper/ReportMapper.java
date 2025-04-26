package com.newbit.newbitfeatureservice.report.query.infrastructure.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;

@Mapper
public interface ReportMapper {
    
    List<ReportDTO> findReports(Map<String, Object> params);
    
    int countReports(Map<String, Object> params);
    
    List<ReportDTO> findAllWithoutPaging();
} 