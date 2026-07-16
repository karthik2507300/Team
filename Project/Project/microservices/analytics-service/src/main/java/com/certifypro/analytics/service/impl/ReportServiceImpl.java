package com.certifypro.analytics.service.impl;

import com.certifypro.analytics.client.ProgramGateway;
import com.certifypro.analytics.client.dto.ProgramDto;
import com.certifypro.analytics.common.ReportScope;
import com.certifypro.analytics.dto.request.GenerateReportRequest;
import com.certifypro.analytics.dto.response.PageResponse;
import com.certifypro.analytics.dto.response.ReportResponse;
import com.certifypro.analytics.entity.ExaminationReport;
import com.certifypro.analytics.repository.ExaminationReportRepository;
import com.certifypro.analytics.service.ReportService;
import com.certifypro.analytics.util.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Report generation and listing.
 *
 * <p><b>Cross-service aggregation note.</b> In the monolith the numeric metrics
 * (RegisteredCandidates, AttendanceRate, PassRate, AvgScore, CertificatesIssued,
 * RenewalComplianceRate, CentreCapacityUtilisation) were computed by reading the
 * SeatAllocation / CandidateResult / Certificate / ProgramEnrolment / TestCentre
 * tables directly. Those tables now belong to exam-service, result-service,
 * certificate-service and candidate-service. The internal endpoints those
 * services expose today are single-entity lookups by id
 * ({@code GET /api/.../internal/{id}}) — none provide the by-window / by-centre /
 * by-program list aggregation required to recompute those numbers. Inventing new
 * internal endpoints is out of scope for this phase.
 *
 * <p>Therefore each numeric metric is recorded as {@code 0} and every report
 * carries a {@code note} explaining that full cross-service aggregation requires
 * additional internal aggregate endpoints (Phase 2). For a Program-scope report
 * we additionally resolve the program label via candidate-service (guarded by a
 * circuit breaker — if it is down the label is simply omitted). The service never
 * fails because a downstream is unavailable.
 */
@Service
public class ReportServiceImpl implements ReportService {

    private static final String PHASE2_NOTE =
            "Numeric metrics are 0: cross-service aggregation (seat allocations, "
                    + "results, certificates, enrolments, centre capacity) requires additional "
                    + "internal aggregate endpoints on exam/result/certificate/candidate services "
                    + "(Phase 2). Only single-entity lookups are exposed today.";

    private final ExaminationReportRepository reportRepository;
    private final ProgramGateway programGateway;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportServiceImpl(ExaminationReportRepository reportRepository,
                             ProgramGateway programGateway) {
        this.reportRepository = reportRepository;
        this.programGateway = programGateway;
    }

    @Override
    @Transactional
    public ReportResponse generate(GenerateReportRequest req) {
        ReportScope scope = parseScope(req.scope());
        Map<String, Object> metrics = switch (scope) {
            case Window -> windowMetrics(req.windowId());
            case Centre -> centreMetrics(req.centreId());
            case Program -> programMetrics(req.programId());
            case Period -> periodMetrics();
        };

        ExaminationReport report = ExaminationReport.builder()
                .scope(scope)
                .metrics(toJson(metrics))
                .generatedDate(LocalDate.now())
                .build();
        return ReportResponse.from(reportRepository.save(report));
    }

    @Override
    public PageResponse<ReportResponse> list(String scope, int page, int limit) {
        if (scope != null) {
            return PageResponse.from(reportRepository.findByScope(parseScope(scope), PageUtil.of(page, limit))
                    .map(ReportResponse::from));
        }
        return PageResponse.from(reportRepository.findAll(PageUtil.of(page, limit))
                .map(ReportResponse::from));
    }

    // --- metric builders -------------------------------------------------

    private Map<String, Object> windowMetrics(Long windowId) {
        Map<String, Object> m = baseScopeId("windowId", windowId);
        // Requires exam-service seat allocations by window + result-service results by window.
        m.put("RegisteredCandidates", 0);
        m.put("AttendanceRate", 0.0);
        m.put("ResultsCount", 0);
        m.put("PassRate", 0.0);
        m.put("AvgScore", 0.0);
        m.put("CertificatesIssued", 0);
        m.put("note", PHASE2_NOTE);
        return m;
    }

    private Map<String, Object> centreMetrics(Long centreId) {
        Map<String, Object> m = baseScopeId("centreId", centreId);
        // Requires exam-service seat allocations by centre + test-centre capacity.
        m.put("RegisteredCandidates", 0);
        m.put("AttendanceRate", 0.0);
        m.put("CentreCapacityUtilisation", 0.0);
        m.put("note", PHASE2_NOTE);
        return m;
    }

    private Map<String, Object> programMetrics(Long programId) {
        Map<String, Object> m = baseScopeId("programId", programId);
        // Program label IS available via candidate-service (guarded Feign).
        if (programId != null) {
            ProgramDto program = programGateway.getProgram(programId);
            if (program != null) {
                m.put("programName", program.programName());
                m.put("level", program.level());
            }
        }
        // Numeric aggregates require enrolments/results/certificates by program.
        m.put("RegisteredCandidates", 0);
        m.put("ResultsCount", 0);
        m.put("PassRate", 0.0);
        m.put("AvgScore", 0.0);
        m.put("CertificatesIssued", 0);
        m.put("note", PHASE2_NOTE);
        return m;
    }

    private Map<String, Object> periodMetrics() {
        Map<String, Object> m = new LinkedHashMap<>();
        // Requires platform-wide counts from certificate/candidate/result services.
        m.put("TotalCertificates", 0);
        m.put("TotalEnrolments", 0);
        m.put("ResultsCount", 0);
        m.put("PassRate", 0.0);
        m.put("AvgScore", 0.0);
        m.put("note", PHASE2_NOTE);
        return m;
    }

    // --- helpers ---------------------------------------------------------

    private Map<String, Object> baseScopeId(String key, Long value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(key, value);
        return m;
    }

    private String toJson(Map<String, Object> metrics) {
        try {
            return objectMapper.writeValueAsString(metrics);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private ReportScope parseScope(String value) {
        try {
            return ReportScope.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid scope: " + value
                    + " (Program, Window, Centre, Period)");
        }
    }
}
