package com.certifypro.analytics.repository;

import com.certifypro.analytics.common.ReportScope;
import com.certifypro.analytics.entity.ExaminationReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExaminationReportRepository extends JpaRepository<ExaminationReport, Long> {

    Page<ExaminationReport> findByScope(ReportScope scope, Pageable p);
}
