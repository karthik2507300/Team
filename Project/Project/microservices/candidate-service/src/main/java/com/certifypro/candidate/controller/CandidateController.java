package com.certifypro.candidate.controller;

import com.certifypro.candidate.dto.request.CreateCandidateRequest;
import com.certifypro.candidate.dto.request.UpdateCandidateRequest;
import com.certifypro.candidate.dto.response.ApiResponse;
import com.certifypro.candidate.dto.response.CandidateResponse;
import com.certifypro.candidate.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    /** Candidate creates their own profile (after registration). */
    @PostMapping
    @PreAuthorize("hasRole('Candidate')")
    public ResponseEntity<ApiResponse<CandidateResponse>> create(@Valid @RequestBody CreateCandidateRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Candidate profile created", candidateService.create(req)));
    }

    /** Current candidate's own profile. Declared before /{id}. */
    @GetMapping("/me")
    @PreAuthorize("hasRole('Candidate')")
    public ResponseEntity<ApiResponse<CandidateResponse>> getMine() {
        return ResponseEntity.ok(ApiResponse.ok(candidateService.getMine()));
    }

    /** View candidate profile (own for candidates; any for staff/Admin). */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(candidateService.getById(id)));
    }

    /** Candidate edits own profile (Admin may edit any). */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateCandidateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Candidate profile updated", candidateService.update(id, req)));
    }
}
