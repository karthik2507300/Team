package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/** level must be one of: Foundation, Associate, Professional, Fellow */
public record CreateProgramRequest(
        @NotBlank String programName,
        String body,
        @NotBlank String level,
        String eligibilityCriteria,
        BigDecimal examFee,
        Integer validityYears,
        Integer maxAttempts
) {
}
