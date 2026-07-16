package com.certifypro.question.dto.response;

import com.certifypro.question.entity.PaperQuestion;

public record PaperQuestionResponse(
        Long paperQuestionId,
        Long questionId,
        Integer sequenceOrder,
        Integer marksAllocated
) {
    public static PaperQuestionResponse from(PaperQuestion pq) {
        return new PaperQuestionResponse(
                pq.getPaperQuestionId(),
                pq.getQuestion() == null ? null : pq.getQuestion().getQuestionId(),
                pq.getSequenceOrder(), pq.getMarksAllocated());
    }
}
