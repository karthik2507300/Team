package com.certifypro.controller;

import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.CandidateResultResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.service.ResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping("/compute/{windowId}")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<List<CandidateResultResponse>>> compute(@PathVariable Long windowId) {
        return ResponseEntity.ok(ApiResponse.ok("Results computed", resultService.compute(windowId)));
    }

    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<CandidateResultResponse>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Result published", resultService.publish(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CandidateResultResponse>>> view(
            @RequestParam(required = false) Long candidateId,
            @RequestParam(required = false) Long windowId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(resultService.view(candidateId, windowId, page, limit)));
    }
}
