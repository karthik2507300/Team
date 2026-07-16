package com.certifypro.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateQuestionRequest(
        @NotNull Long programId,
        String topicTag,
        @NotBlank String difficulty,
        @NotBlank String questionText,
        @NotBlank String type,
        @PositiveOrZero Integer marks
) {
}
