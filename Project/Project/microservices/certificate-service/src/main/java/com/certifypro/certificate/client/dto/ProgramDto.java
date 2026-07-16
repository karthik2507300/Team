package com.certifypro.certificate.client.dto;

/**
 * Local copy of candidate-service's program view (raw DTO from
 * GET /api/programs/internal/{programId}). Used to resolve validityYears when
 * issuing / renewing a certificate.
 */
public record ProgramDto(
        Long programId,
        String programName,
        String level,
        Integer validityYears,
        Integer maxAttempts
) {
}
