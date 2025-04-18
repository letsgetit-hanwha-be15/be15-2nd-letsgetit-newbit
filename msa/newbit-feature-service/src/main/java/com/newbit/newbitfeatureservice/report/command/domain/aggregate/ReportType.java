package com.newbit.newbitfeatureservice.report.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_type_id")
    private Long id;

    @Column(name = "report_type_name", nullable = false, length = 50)
    private String name;
    
    @Builder
    public ReportType(String name) {
        this.name = name;
    }
} 