package com.certifypro.analytics.client.dto;

/**
 * Local copy of the program shape returned by candidate-service's internal
 * endpoint {@code GET /api/programs/internal/{programId}}. Only the descriptive
 * fields are used to label the report; extra fields mirror the source DTO for
 * tolerant JSON decoding.
 */
public record ProgramDto(
        Long programId,
        String programName,
        String level,
        Integer validityYears,
        Integer maxAttempts
) {
}
