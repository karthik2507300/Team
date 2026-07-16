package com.certifypro.dto.response;

import com.certifypro.model.QuestionPaper;

import java.util.List;

public record QuestionPaperResponse(
        Long paperId,
        Long windowId,
        Long programId,
        String paperCode,
        Integer totalMarks,
        Integer duration,
        String instructionsRef,
        Long createdById,
        String status,
        List<PaperQuestionResponse> questions
) {
    public static QuestionPaperResponse from(QuestionPaper p, List<PaperQuestionResponse> questions) {
        return new QuestionPaperResponse(
                p.getPaperId(), p.getWindowId(), p.getProgramId(), p.getPaperCode(),
                p.getTotalMarks(), p.getDuration(), p.getInstructionsRef(), p.getCreatedById(),
                p.getStatus() == null ? null : p.getStatus().name(), questions);
    }
}
