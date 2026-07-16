package com.certifypro.certificate.controller;

import com.certifypro.certificate.dto.internal.InternalIssueCertificateRequest;
import com.certifypro.certificate.dto.response.CertificateResponse;
import com.certifypro.certificate.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service-to-service endpoints (called via Feign, not by the browser).
 * Returns raw DTOs (no ApiResponse envelope). Permitted without a role in
 * SecurityConfig via /api/**&#47;internal/**. result-service calls /issue to
 * auto-issue a certificate when a Pass result is published.
 */
@RestController
@RequestMapping("/api/certificates/internal")
public class InternalCertificateController {

    private final CertificateService certificateService;

    public InternalCertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/issue")
    public ResponseEntity<CertificateResponse> issue(@Valid @RequestBody InternalIssueCertificateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificateService.issueInternal(req.candidateId(), req.programId()));
    }
}
