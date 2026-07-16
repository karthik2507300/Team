package com.certifypro.controller;

import com.certifypro.dto.request.CreateQuestionRequest;
import com.certifypro.dto.request.UpdateQuestionRequest;
import com.certifypro.dto.response.ApiResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.QuestionResponse;
import com.certifypro.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<QuestionResponse>> create(@Valid @RequestBody CreateQuestionRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Question created", questionService.create(req)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> filter(
            @RequestParam(required = false) Long programId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(
                questionService.filter(programId, difficulty, type, page, limit)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<QuestionResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateQuestionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Question updated", questionService.update(id, req)));
    }
}
