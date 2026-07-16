package com.certifypro.service;

import com.certifypro.dto.request.GenerateReportRequest;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.ReportResponse;
import com.certifypro.model.*;
import com.certifypro.model.enums.CertificateStatus;
import com.certifypro.model.enums.ReportScope;
import com.certifypro.model.enums.ResultOutcome;
import com.certifypro.model.enums.SeatStatus;
import com.certifypro.repository.*;
import com.certifypro.util.PageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final ExaminationReportRepository reportRepository;
    private final SeatAllocationRepository seatRepository;
    private final CandidateResultRepository resultRepository;
    private final CertificateRepository certificateRepository;
    private final ProgramEnrolmentRepository enrolmentRepository;
    private final TestCentreRepository testCentreRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportService(ExaminationReportRepository reportRepository,
                         SeatAllocationRepository seatRepository,
                         CandidateResultRepository resultRepository,
                         CertificateRepository certificateRepository,
                         ProgramEnrolmentRepository enrolmentRepository,
                         TestCentreRepository testCentreRepository) {
        this.reportRepository = reportRepository;
        this.seatRepository = seatRepository;
        this.resultRepository = resultRepository;
        this.certificateRepository = certificateRepository;
        this.enrolmentRepository = enrolmentRepository;
        this.testCentreRepository = testCentreRepository;
    }

    @Transactional
    public ReportResponse generate(GenerateReportRequest req) {
        ReportScope scope = parseScope(req.scope());
        Map<String, Object> metrics = switch (scope) {
            case Window -> windowMetrics(req.windowId());
            case Centre -> centreMetrics(req.centreId());
            case Program -> programMetrics(req.programId());
            case Period -> periodMetrics();
        };

        ExaminationReport report = new ExaminationReport();
        report.setScope(scope);
        report.setMetrics(toJson(metrics));
        report.setGeneratedDate(LocalDate.now());
        return ReportResponse.from(reportRepository.save(report));
    }

    public PageResponse<ReportResponse> list(String scope, int page, int limit) {
        if (scope != null) {
            return PageResponse.from(reportRepository.findByScope(parseScope(scope), PageUtil.of(page, limit))
                    .map(ReportResponse::from));
        }
        return PageResponse.from(reportRepository.findAll(PageUtil.of(page, limit))
                .map(ReportResponse::from));
    }

    private Map<String, Object> windowMetrics(Long windowId) {
        List<SeatAllocation> seats = windowId == null ? List.of() : seatRepository.findByWindowId(windowId);
        List<CandidateResult> results = windowId == null ? List.of() : resultRepository.findAllByWindowId(windowId);
        long attended = seats.stream().filter(s -> s.getStatus() != SeatStatus.NoShow).count();

        Map<String, Object> m = baseScopeId("windowId", windowId);
        m.put("RegisteredCandidates", seats.size());
        m.put("AttendanceRate", pct(attended, seats.size()));
        addResultMetrics(m, results);
        m.put("CertificatesIssued", results.stream().filter(r -> r.getOutcome() == ResultOutcome.Pass).count());
        return m;
    }

    private Map<String, Object> centreMetrics(Long centreId) {
        List<SeatAllocation> seats = centreId == null ? List.of() : seatRepository.findByCentreId(centreId);
        long attended = seats.stream().filter(s -> s.getStatus() != SeatStatus.NoShow).count();
        TestCentre centre = centreId == null ? null : testCentreRepository.findById(centreId).orElse(null);
        int capacity = centre == null || centre.getCapacity() == null ? 0 : centre.getCapacity();

        Map<String, Object> m = baseScopeId("centreId", centreId);
        m.put("RegisteredCandidates", seats.size());
        m.put("AttendanceRate", pct(attended, seats.size()));
        m.put("CentreCapacityUtilisation", pct(seats.size(), capacity));
        return m;
    }

    private Map<String, Object> programMetrics(Long programId) {
        List<ProgramEnrolment> enrolments = programId == null ? List.of()
                : enrolmentRepository.findAllByProgramId(programId);
        List<CandidateResult> results = programId == null ? List.of()
                : resultRepository.findAllByProgramId(programId);
        long certs = programId == null ? 0
                : certificateRepository.findAllByProgramId(programId).stream()
                .filter(c -> c.getStatus() == CertificateStatus.Valid).count();

        Map<String, Object> m = baseScopeId("programId", programId);
        m.put("RegisteredCandidates", enrolments.size());
        addResultMetrics(m, results);
        m.put("CertificatesIssued", certs);
        return m;
    }

    private Map<String, Object> periodMetrics() {
        List<CandidateResult> results = resultRepository.findAll();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("TotalCertificates", certificateRepository.count());
        m.put("TotalEnrolments", enrolmentRepository.count());
        addResultMetrics(m, results);
        return m;
    }

    private void addResultMetrics(Map<String, Object> m, List<CandidateResult> results) {
        long pass = results.stream().filter(r -> r.getOutcome() == ResultOutcome.Pass).count();
        double avg = results.stream()
                .filter(r -> r.getPercentage() != null)
                .mapToDouble(CandidateResult::getPercentage).average().orElse(0);
        m.put("ResultsCount", results.size());
        m.put("PassRate", pct(pass, results.size()));
        m.put("AvgScore", round2(avg));
    }

    private Map<String, Object> baseScopeId(String key, Long value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(key, value);
        return m;
    }

    private double pct(long part, long whole) {
        return whole == 0 ? 0.0 : round2(part * 100.0 / whole);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
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
