package com.certifypro.question.controller;

import com.certifypro.question.dto.response.PaperDto;
import com.certifypro.question.service.QuestionPaperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service-to-service endpoint (called via Feign, not by the browser).
 * Returns a raw DTO (no ApiResponse envelope) for simple Feign decoding.
 * Permitted without a role in SecurityConfig ("/api/**&#47;internal/**").
 * result-service calls this to obtain a paper's totalMarks for grading.
 */
@RestController
@RequestMapping("/api/question-papers/internal")
public class InternalPaperController {

    private final QuestionPaperService paperService;

    public InternalPaperController(QuestionPaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping("/{paperId}")
    public PaperDto getByPaperId(@PathVariable Long paperId) {
        return paperService.getInternal(paperId);
    }
}
