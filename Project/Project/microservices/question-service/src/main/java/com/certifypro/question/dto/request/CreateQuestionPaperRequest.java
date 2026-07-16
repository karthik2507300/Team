package com.certifypro.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQuestionPaperRequest(
        @NotNull Long windowId,
        @NotNull Long programId,
        @NotBlank String paperCode,
        Integer duration,
        String instructionsRef
) {
}
