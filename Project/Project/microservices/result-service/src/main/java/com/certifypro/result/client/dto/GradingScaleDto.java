package com.certifypro.result.client.dto;

/**
 * Local copy of candidate-service's grading band (raw DTO from
 * GET /api/programs/internal/{programId}/grading-scale). Used to map a percentage
 * to a grade letter and pass/fail outcome during result compute.
 */
public record GradingScaleDto(
        String gradeLetter,
        Float minPercentage,
        Float maxPercentage,
        Boolean isPassing
) {
}
