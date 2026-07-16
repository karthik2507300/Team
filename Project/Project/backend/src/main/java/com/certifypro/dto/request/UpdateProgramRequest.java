package com.certifypro.dto.request;

import java.math.BigDecimal;

/** Edit a program. Null fields are left unchanged. status may be Active/Discontinued. */
public record UpdateProgramRequest(
        String programName,
        String body,
        String level,
        String eligibilityCriteria,
        BigDecimal examFee,
        Integer validityYears,
        Integer maxAttempts,
        String status
) {
}
