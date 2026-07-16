package com.certifypro.dto.response;

import com.certifypro.model.PaperQuestion;

public record PaperQuestionResponse(
        Long paperQuestionId,
        Long questionId,
        Integer sequenceOrder,
        Integer marksAllocated
) {
    public static PaperQuestionResponse from(PaperQuestion pq) {
        return new PaperQuestionResponse(
                pq.getPaperQuestionId(), pq.getQuestionId(),
                pq.getSequenceOrder(), pq.getMarksAllocated());
    }
}
