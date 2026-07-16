package com.certifypro.candidate.dto.internal;

import com.certifypro.candidate.entity.CertificationProgram;

/** Compact program view for service-to-service consumers (e.g. exam / question service). */
public record ProgramDto(
        Long programId,
        String programName,
        String level,
        Integer validityYears,
        Integer maxAttempts
) {
    public static ProgramDto from(CertificationProgram p) {
        return new ProgramDto(
                p.getProgramId(), p.getProgramName(),
                p.getLevel() == null ? null : p.getLevel().name(),
                p.getValidityYears(), p.getMaxAttempts());
    }
}
