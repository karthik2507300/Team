package com.certifypro.certificate.controller;

import com.certifypro.certificate.dto.request.IssueCertificateRequest;
import com.certifypro.certificate.dto.request.UpdateCertificateStatusRequest;
import com.certifypro.certificate.dto.response.ApiResponse;
import com.certifypro.certificate.dto.response.CertificateResponse;
import com.certifypro.certificate.service.CertificateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /** Certification Officer issues a certificate. */
    @PostMapping
    @PreAuthorize("hasAnyRole('CertificationOfficer','Admin')")
    public ResponseEntity<ApiResponse<CertificateResponse>> issue(
            @Valid @RequestBody IssueCertificateRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Certificate issued", certificateService.issue(req)));
    }

    /** Validity tracker — certificates expiring within 90 days (officer view). Declared before /{candidateId}. */
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('CertificationOfficer','Admin')")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> expiring() {
        return ResponseEntity.ok(ApiResponse.ok(certificateService.expiringWithin90Days()));
    }

    /** Candidate views own certificates (officer/Admin may view any). */
    @GetMapping("/{candidateId}")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> byCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(ApiResponse.ok(certificateService.getByCandidate(candidateId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('CertificationOfficer','Admin')")
    public ResponseEntity<ApiResponse<CertificateResponse>> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateCertificateStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Certificate status updated",
                certificateService.updateStatus(id, req)));
    }
}
