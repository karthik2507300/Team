package com.certifypro.dto.response;

import com.certifypro.model.ExaminationReport;

import java.time.LocalDate;

public record ReportResponse(
        Long reportId,
        String scope,
        String metrics,
        LocalDate generatedDate
) {
    public static ReportResponse from(ExaminationReport r) {
        return new ReportResponse(
                r.getReportId(),
                r.getScope() == null ? null : r.getScope().name(),
                r.getMetrics(),
                r.getGeneratedDate());
    }
}
