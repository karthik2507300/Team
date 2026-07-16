package com.certifypro.exam.client.dto;

/** Local copy of candidate-service's certification program projection. */
public record ProgramDto(
        Long programId,
        String programName,
        String level,
        Integer validityYears,
        Integer maxAttempts
) {
}
