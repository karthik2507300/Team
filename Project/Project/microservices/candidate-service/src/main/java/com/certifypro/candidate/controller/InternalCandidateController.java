package com.certifypro.candidate.controller;

import com.certifypro.candidate.dto.internal.RegisterCandidateRequest;
import com.certifypro.candidate.dto.response.CandidateResponse;
import com.certifypro.candidate.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Service-to-service endpoints (called via Feign, not by the browser).
 * Returns raw DTOs (no ApiResponse envelope). Permitted without a role in
 * SecurityConfig via /api/**&#47;internal/**. auth-service calls /register at
 * candidate self-registration.
 */
@RestController
@RequestMapping("/api/candidates/internal")
public class InternalCandidateController {

    private final CandidateService candidateService;

    public InternalCandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping("/register")
    public ResponseEntity<CandidateResponse> register(@RequestBody RegisterCandidateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.register(req));
    }

    @GetMapping("/{candidateId}")
    public CandidateResponse getById(@PathVariable Long candidateId) {
        return candidateService.getById(candidateId);
    }
}
