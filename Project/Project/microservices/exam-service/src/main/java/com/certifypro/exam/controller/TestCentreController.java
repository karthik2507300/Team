package com.certifypro.exam.controller;

import com.certifypro.exam.dto.request.CreateTestCentreRequest;
import com.certifypro.exam.dto.request.UpdateTestCentreRequest;
import com.certifypro.exam.dto.response.ApiResponse;
import com.certifypro.exam.dto.response.PageResponse;
import com.certifypro.exam.dto.response.TestCentreResponse;
import com.certifypro.exam.service.TestCentreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-centres")
public class TestCentreController {

    private final TestCentreService testCentreService;

    public TestCentreController(TestCentreService testCentreService) {
        this.testCentreService = testCentreService;
    }

    @PostMapping
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<TestCentreResponse>> create(@Valid @RequestBody CreateTestCentreRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Test centre created", testCentreService.create(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TestCentreResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(testCentreService.list(page, limit)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<ApiResponse<TestCentreResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateTestCentreRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Test centre updated", testCentreService.update(id, req)));
    }
}
