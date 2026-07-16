package com.certifypro.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Replaces the grading scale for a program with the supplied bands. */
public record GradingScaleRequest(
        @NotEmpty @Valid List<GradeBand> bands
) {
    public record GradeBand(
            @NotNull String gradeLetter,
            @NotNull Integer minPercentage,
            @NotNull Integer maxPercentage,
            @NotNull Boolean isPassing
    ) {
    }
}
