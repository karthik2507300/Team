package com.certifypro.exam.controller;

import com.certifypro.exam.dto.response.ExamWindowResponse;
import com.certifypro.exam.service.ExamWindowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service-to-service endpoints (called via Feign, not by the browser).
 * Returns raw DTOs (no ApiResponse envelope). Permitted without a role in
 * SecurityConfig via the /api/**&#47;internal/** matcher.
 */
@RestController
@RequestMapping("/api/exam-windows/internal")
public class InternalExamWindowController {

    private final ExamWindowService examWindowService;

    public InternalExamWindowController(ExamWindowService examWindowService) {
        this.examWindowService = examWindowService;
    }

    @GetMapping("/{windowId}")
    public ExamWindowResponse getById(@PathVariable Long windowId) {
        return examWindowService.getById(windowId);
    }
}
