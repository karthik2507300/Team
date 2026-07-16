package com.certifypro.candidate.dto.response;

import com.certifypro.candidate.entity.CertificationProgram;

import java.math.BigDecimal;

public record ProgramResponse(
        Long programId,
        String programName,
        String body,
        String level,
        String eligibilityCriteria,
        BigDecimal examFee,
        Integer validityYears,
        Integer maxAttempts,
        String status
) {
    public static ProgramResponse from(CertificationProgram p) {
        return new ProgramResponse(
                p.getProgramId(), p.getProgramName(), p.getBody(),
                p.getLevel() == null ? null : p.getLevel().name(),
                p.getEligibilityCriteria(), p.getExamFee(), p.getValidityYears(),
                p.getMaxAttempts(), p.getStatus() == null ? null : p.getStatus().name());
    }
}
