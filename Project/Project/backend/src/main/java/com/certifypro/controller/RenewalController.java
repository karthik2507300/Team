package com.certifypro.controller;

import com.certifypro.dto.request.CreateRenewalRequest;
import com.certifypro.dto.request.ReviewRenewalRequest;
import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.RenewalResponse;
import com.certifypro.service.RenewalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/renewals")
public class RenewalController {

    private final RenewalService renewalService;

    public RenewalController(RenewalService renewalService) {
        this.renewalService = renewalService;
    }

    @PostMapping
    @PreAuthorize("hasRole('Candidate')")
    public ResponseEntity<ApiResponse<RenewalResponse>> submit(@Valid @RequestBody CreateRenewalRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Renewal application submitted", renewalService.submit(req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RenewalResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(renewalService.getById(id)));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('CertificationOfficer','Admin')")
    public ResponseEntity<ApiResponse<RenewalResponse>> review(
            @PathVariable Long id, @Valid @RequestBody ReviewRenewalRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Renewal reviewed", renewalService.review(id, req)));
    }
}
