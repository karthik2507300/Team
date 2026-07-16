package com.certifypro.result.controller;

import com.certifypro.result.dto.request.CreateMarksEntryRequest;
import com.certifypro.result.dto.response.ApiResponse;
import com.certifypro.result.dto.response.MarksEntryResponse;
import com.certifypro.result.dto.response.PageResponse;
import com.certifypro.result.service.MarksEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marks-entries")
public class MarksEntryController {

    private final MarksEntryService marksEntryService;

    public MarksEntryController(MarksEntryService marksEntryService) {
        this.marksEntryService = marksEntryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Evaluator','ExamController','Admin')")
    public ResponseEntity<ApiResponse<MarksEntryResponse>> submit(
            @Valid @RequestBody CreateMarksEntryRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Marks submitted", marksEntryService.submit(req)));
    }

    /** Moderation queue (status defaults to Moderated) or per-script marks. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ExamController','Admin','Evaluator')")
    public ResponseEntity<ApiResponse<PageResponse<MarksEntryResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long scriptId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(marksEntryService.list(status, scriptId, page, limit)));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<MarksEntryResponse>> verify(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Marks verified", marksEntryService.verify(id)));
    }

    @PatchMapping("/{id}/moderate")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<MarksEntryResponse>> moderate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Marks moderated", marksEntryService.moderate(id)));
    }
}
