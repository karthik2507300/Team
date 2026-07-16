package com.certifypro.analytics.controller;

import com.certifypro.analytics.dto.request.GenerateReportRequest;
import com.certifypro.analytics.dto.response.ApiResponse;
import com.certifypro.analytics.dto.response.PageResponse;
import com.certifypro.analytics.dto.response.ReportResponse;
import com.certifypro.analytics.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('Admin','ExamController','CertificationOfficer')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReportResponse>>> list(
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) Long windowId,
            @RequestParam(required = false) Long centreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.list(scope, page, limit)));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<ReportResponse>> generate(@Valid @RequestBody GenerateReportRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Report generated", reportService.generate(req)));
    }
}
