package com.certifypro.controller;

import com.certifypro.dto.request.CreateEnrolmentRequest;
import com.certifypro.dto.request.UpdateEligibilityRequest;
import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.EnrolmentResponse;
import com.certifypro.service.EnrolmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrolments")
public class EnrolmentController {

    private final EnrolmentService enrolmentService;

    public EnrolmentController(EnrolmentService enrolmentService) {
        this.enrolmentService = enrolmentService;
    }

    /** Candidate enrols in a program. */
    @PostMapping
    @PreAuthorize("hasRole('Candidate')")
    public ResponseEntity<ApiResponse<EnrolmentResponse>> create(@Valid @RequestBody CreateEnrolmentRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Enrolment created", enrolmentService.create(req)));
    }

    /** Admin: list enrolments, optionally filtered by eligibilityStatus (verifier queue). */
    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<com.certifypro.dto.response.PageResponse<EnrolmentResponse>>> list(
            @RequestParam(required = false) String eligibilityStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(enrolmentService.list(eligibilityStatus, page, limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrolmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(enrolmentService.getById(id)));
    }

    /** Admin verifies eligibility. */
    @PatchMapping("/{id}/eligibility")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<EnrolmentResponse>> updateEligibility(
            @PathVariable Long id, @Valid @RequestBody UpdateEligibilityRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Eligibility updated",
                enrolmentService.updateEligibility(id, req)));
    }
}
