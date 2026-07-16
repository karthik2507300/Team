package com.certifypro.controller;

import com.certifypro.dto.request.CreateScriptAllocationRequest;
import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.ScriptAllocationResponse;
import com.certifypro.service.ScriptAllocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/script-allocations")
public class ScriptAllocationController {

    private final ScriptAllocationService scriptAllocationService;

    public ScriptAllocationController(ScriptAllocationService scriptAllocationService) {
        this.scriptAllocationService = scriptAllocationService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<ScriptAllocationResponse>> assign(
            @Valid @RequestBody CreateScriptAllocationRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Script allocated", scriptAllocationService.assign(req)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('Evaluator','ExamController','Admin')")
    public ResponseEntity<ApiResponse<PageResponse<ScriptAllocationResponse>>> list(
            @RequestParam(required = false) Long evaluatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(
                scriptAllocationService.listByEvaluator(evaluatorId, page, limit)));
    }
}
