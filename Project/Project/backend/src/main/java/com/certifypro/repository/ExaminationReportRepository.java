package com.certifypro.repository;

import com.certifypro.model.ExaminationReport;
import com.certifypro.model.enums.ReportScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExaminationReportRepository extends JpaRepository<ExaminationReport, Long> {

    Page<ExaminationReport> findByScope(ReportScope scope, Pageable p);
}
