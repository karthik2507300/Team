package com.certifypro.question.dto.response;

import com.certifypro.question.entity.Question;

public record QuestionResponse(
        Long questionId,
        Long programId,
        String topicTag,
        String difficulty,
        String questionText,
        String type,
        Integer marks,
        Long createdById,
        String status
) {
    public static QuestionResponse from(Question q) {
        return new QuestionResponse(
                q.getQuestionId(), q.getProgramId(), q.getTopicTag(),
                q.getDifficulty() == null ? null : q.getDifficulty().name(),
                q.getQuestionText(),
                q.getType() == null ? null : q.getType().name(),
                q.getMarks(), q.getCreatedById(),
                q.getStatus() == null ? null : q.getStatus().name());
    }
}
