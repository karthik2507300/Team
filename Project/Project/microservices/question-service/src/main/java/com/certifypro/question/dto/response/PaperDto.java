package com.certifypro.question.dto.response;

import com.certifypro.question.entity.QuestionPaper;

/**
 * Raw service-to-service payload returned by the internal endpoint
 * {@code GET /api/question-papers/internal/{paperId}}. Consumed by result-service
 * (via its PaperClient) to obtain a paper's totalMarks for grading.
 */
public record PaperDto(
        Long paperId,
        Long windowId,
        Long programId,
        Integer totalMarks
) {
    public static PaperDto from(QuestionPaper p) {
        return new PaperDto(p.getPaperId(), p.getWindowId(), p.getProgramId(), p.getTotalMarks());
    }
}
