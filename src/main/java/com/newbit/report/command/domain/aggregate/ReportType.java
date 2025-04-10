package com.newbit.report.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "report_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportType {

    @Id
    @Column(name = "report_type_id")
    private Long id;

    @Column(name = "report_type_name", nullable = false, length = 50)
    private String name;

} 