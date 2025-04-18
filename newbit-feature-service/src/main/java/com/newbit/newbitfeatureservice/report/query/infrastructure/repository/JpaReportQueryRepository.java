package com.newbit.newbitfeatureservice.report.query.infrastructure.repository;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.newbit.newbitfeatureservice.report.command.domain.aggregate.Report;
import com.newbit.newbitfeatureservice.report.command.domain.aggregate.ReportStatus;
import com.newbit.newbitfeatureservice.report.query.domain.repository.ReportQueryRepository;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportDTO;
import com.newbit.newbitfeatureservice.report.query.dto.response.ReportTypeDTO;

@Repository
public class JpaReportQueryRepository implements ReportQueryRepository {

    private final ReportJpaRepository reportJpaRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public JpaReportQueryRepository(ReportJpaRepository reportJpaRepository) {
        this.reportJpaRepository = reportJpaRepository;
    }

    @Override
    public Page<ReportDTO> findReports(ReportStatus status, Pageable pageable) {
        Page<Report> reportPage;
        if (status != null) {
            reportPage = reportJpaRepository.findByStatus(status, pageable);
        } else {
            reportPage = reportJpaRepository.findAll(pageable);
        }
        
        return convertToReportDTOPage(reportPage, pageable);
    }

    @Override
    public List<ReportDTO> findAllWithoutPaging() {
        return reportJpaRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<ReportDTO> findReportsByPostId(Long postId, Pageable pageable) {
        Page<Report> reportPage = reportJpaRepository.findByPostId(postId, pageable);
        return convertToReportDTOPage(reportPage, pageable);
    }
    
    @Override
    public Page<ReportDTO> findReportsByCommentId(Long commentId, Pageable pageable) {
        Page<Report> reportPage = reportJpaRepository.findByCommentId(commentId, pageable);
        return convertToReportDTOPage(reportPage, pageable);
    }
    
    @Override
    public Page<ReportDTO> findReportsByReporterId(Long userId, Pageable pageable) {
        Page<Report> reportPage = reportJpaRepository.findByUserId(userId, pageable);
        return convertToReportDTOPage(reportPage, pageable);
    }
    
    @Override
    public Page<ReportDTO> findReportsByReportTypeId(Long reportTypeId, Pageable pageable) {
        String jpql = "SELECT r FROM Report r JOIN r.reportType rt WHERE rt.id = :reportTypeId";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class)
                .setParameter("reportTypeId", reportTypeId);
                
        return executePagedQuery(query, pageable);
    }
    
    @Override
    public Page<ReportDTO> findReportsByStatusAndReportTypeId(ReportStatus status, Long reportTypeId, Pageable pageable) {
        String jpql = "SELECT r FROM Report r JOIN r.reportType rt WHERE r.status = :status AND rt.id = :reportTypeId";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class)
                .setParameter("status", status)
                .setParameter("reportTypeId", reportTypeId);
                
        return executePagedQuery(query, pageable);
    }
    
    @Override
    public Page<ReportDTO> findReportsByPostUserId(Long userId, Pageable pageable) {
        String jpql = "SELECT r FROM Report r, com.newbit.post.entity.Post p WHERE r.postId = p.id AND p.userId = :userId";
        TypedQuery<Report> query = entityManager.createQuery(jpql, Report.class)
                .setParameter("userId", userId);
                
        return executePagedQuery(query, pageable);
    }

    @Override
    public Page<ReportDTO> findReportsByCommentUserId(Long userId, Pageable pageable) {
        Page<Report> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        return convertToReportDTOPage(emptyPage, pageable);
    }

    @Override
    public Page<ReportDTO> findReportsByContentUserId(Long userId, Pageable pageable) {
        return findReportsByPostUserId(userId, pageable);
    }
    
    private Page<ReportDTO> executePagedQuery(TypedQuery<Report> query, Pageable pageable) {
        String countQueryString = query.unwrap(org.hibernate.query.Query.class).getQueryString();
        countQueryString = "SELECT COUNT(r) " + countQueryString.substring(countQueryString.indexOf("FROM"));
        
        int orderByIndex = countQueryString.toUpperCase().indexOf("ORDER BY");
        if (orderByIndex > 0) {
            countQueryString = countQueryString.substring(0, orderByIndex);
        }
        
        TypedQuery<Long> countQuery = entityManager.createQuery(countQueryString, Long.class);
        query.getParameters().forEach(param -> {
            countQuery.setParameter(param.getName(), query.getParameterValue(param.getName()));
        });
        
        Long totalCount = countQuery.getSingleResult();
        
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<Report> reports = query.getResultList();
        List<ReportDTO> reportDTOs = reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(reportDTOs, pageable, totalCount);
    }
    
    private ReportDTO convertToDTO(Report report) {
        ReportTypeDTO reportTypeDTO = new ReportTypeDTO(
            report.getReportType().getId(),
            report.getReportType().getName()
        );
        
        return new ReportDTO(
            report.getReportId(),
            report.getUserId(),
            report.getCommentId(),
            report.getPostId(),
            reportTypeDTO,
            report.getContent(),
            report.getStatus(),
            report.getCreatedAt(),
            report.getUpdatedAt()
        );
    }
    
    private Page<ReportDTO> convertToReportDTOPage(Page<Report> reportPage, Pageable pageable) {
        List<ReportDTO> reportDTOs = reportPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(reportDTOs, pageable, reportPage.getTotalElements());
    }
} 