package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReEvaluationRequest(
        @NotNull Long resultId,
        @NotBlank String reason
) {
}
