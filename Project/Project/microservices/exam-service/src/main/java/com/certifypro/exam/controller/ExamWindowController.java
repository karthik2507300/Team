package com.certifypro.exam.controller;

import com.certifypro.exam.dto.request.CreateExamWindowRequest;
import com.certifypro.exam.dto.request.UpdateExamWindowRequest;
import com.certifypro.exam.dto.response.ApiResponse;
import com.certifypro.exam.dto.response.ExamWindowResponse;
import com.certifypro.exam.dto.response.PageResponse;
import com.certifypro.exam.service.ExamWindowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exam-windows")
public class ExamWindowController {

    private final ExamWindowService examWindowService;

    public ExamWindowController(ExamWindowService examWindowService) {
        this.examWindowService = examWindowService;
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<ExamWindowResponse>> create(@Valid @RequestBody CreateExamWindowRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Exam window created", examWindowService.create(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ExamWindowResponse>>> list(
            @RequestParam(required = false) Long programId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(examWindowService.list(programId, page, limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamWindowResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(examWindowService.getById(id)));
    }

    /** Edit / open / close a window (Admin). Not in the original list but needed by the Admin console. */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<ExamWindowResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateExamWindowRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Exam window updated", examWindowService.update(id, req)));
    }
}
