package com.certifypro.question.controller;

import com.certifypro.question.dto.request.AddPaperQuestionsRequest;
import com.certifypro.question.dto.request.CreateQuestionPaperRequest;
import com.certifypro.question.dto.request.UpdatePaperStatusRequest;
import com.certifypro.question.dto.response.ApiResponse;
import com.certifypro.question.dto.response.QuestionPaperResponse;
import com.certifypro.question.service.QuestionPaperService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-papers")
public class QuestionPaperController {

    private final QuestionPaperService paperService;

    public QuestionPaperController(QuestionPaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<QuestionPaperResponse>> create(
            @Valid @RequestBody CreateQuestionPaperRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Question paper created", paperService.create(req)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<QuestionPaperResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(paperService.getById(id)));
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<QuestionPaperResponse>> addQuestions(
            @PathVariable Long id, @Valid @RequestBody AddPaperQuestionsRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Questions added", paperService.addQuestions(id, req)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ExamController','Admin')")
    public ResponseEntity<ApiResponse<QuestionPaperResponse>> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdatePaperStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Paper status updated", paperService.updateStatus(id, req)));
    }
}
