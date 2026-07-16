package com.certifypro.result.controller;

import com.certifypro.result.dto.request.CreateReEvaluationRequest;
import com.certifypro.result.dto.response.ApiResponse;
import com.certifypro.result.dto.response.ReEvaluationResponse;
import com.certifypro.result.service.ReEvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/re-evaluation-requests")
public class ReEvaluationController {

    private final ReEvaluationService reEvaluationService;

    public ReEvaluationController(ReEvaluationService reEvaluationService) {
        this.reEvaluationService = reEvaluationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('Candidate')")
    public ResponseEntity<ApiResponse<ReEvaluationResponse>> submit(
            @Valid @RequestBody CreateReEvaluationRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Re-evaluation request submitted", reEvaluationService.submit(req)));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<ReEvaluationResponse>> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Re-evaluation resolved", reEvaluationService.resolve(id)));
    }
}
