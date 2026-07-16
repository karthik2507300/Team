package com.certifypro.dto.request;

/** Edit a question. Null fields left unchanged. status: Active/Retired/UnderReview. */
public record UpdateQuestionRequest(
        String topicTag,
        String difficulty,
        String questionText,
        String type,
        Integer marks,
        String status
) {
}
