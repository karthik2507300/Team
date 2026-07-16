package com.certifypro.result.client.dto;

/**
 * Local copy of question-service's paper view (raw DTO from
 * GET /api/question-papers/internal/{paperId}). Supplies the paper's totalMarks
 * (used in dual-marking moderation and result compute) plus the window/program it
 * belongs to (used to group script-allocations for a window during compute).
 */
public record PaperDto(
        Long paperId,
        Long windowId,
        Long programId,
        Integer totalMarks
) {
}
