package com.certifypro.exam.controller;

import com.certifypro.exam.dto.request.CreateInvigilatorAssignmentRequest;
import com.certifypro.exam.dto.response.ApiResponse;
import com.certifypro.exam.dto.response.InvigilatorAssignmentResponse;
import com.certifypro.exam.service.InvigilatorAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invigilator-assignments")
public class InvigilatorAssignmentController {

    private final InvigilatorAssignmentService assignmentService;

    public InvigilatorAssignmentController(InvigilatorAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CentreAdmin','Admin')")
    public ResponseEntity<ApiResponse<InvigilatorAssignmentResponse>> assign(
            @Valid @RequestBody CreateInvigilatorAssignmentRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Invigilator assigned", assignmentService.assign(req)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CentreAdmin','Admin')")
    public ResponseEntity<ApiResponse<List<InvigilatorAssignmentResponse>>> list(
            @RequestParam(required = false) Long windowId,
            @RequestParam(required = false) Long centreId) {
        return ResponseEntity.ok(ApiResponse.ok(assignmentService.list(windowId, centreId)));
    }
}
