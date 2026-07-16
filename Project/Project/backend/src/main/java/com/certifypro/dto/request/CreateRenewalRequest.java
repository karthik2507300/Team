package com.certifypro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Candidate submits a renewal application with self-declared CPD points. */
public record CreateRenewalRequest(
        @NotNull Long certificateId,
        @NotNull @PositiveOrZero Integer cpdPointsSubmitted
) {
}
