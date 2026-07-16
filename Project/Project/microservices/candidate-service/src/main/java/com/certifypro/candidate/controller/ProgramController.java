package com.certifypro.candidate.controller;

import com.certifypro.candidate.dto.request.CreateProgramRequest;
import com.certifypro.candidate.dto.request.GradingScaleRequest;
import com.certifypro.candidate.dto.request.UpdateProgramRequest;
import com.certifypro.candidate.dto.response.ApiResponse;
import com.certifypro.candidate.dto.response.GradingScaleResponse;
import com.certifypro.candidate.dto.response.PageResponse;
import com.certifypro.candidate.dto.response.ProgramResponse;
import com.certifypro.candidate.service.ProgramService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    /** List active certification programs (any authenticated user). */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProgramResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(programService.listActive(page, limit)));
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<ProgramResponse>> create(@Valid @RequestBody CreateProgramRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Program created", programService.create(req)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<ProgramResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateProgramRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Program updated", programService.update(id, req)));
    }

    @PostMapping("/{id}/grading-scale")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<List<GradingScaleResponse>>> setGradingScale(
            @PathVariable Long id, @Valid @RequestBody GradingScaleRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Grading scale saved",
                programService.setGradingScale(id, req)));
    }

    @GetMapping("/{id}/grading-scale")
    public ResponseEntity<ApiResponse<List<GradingScaleResponse>>> getGradingScale(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(programService.getGradingScale(id)));
    }
}
