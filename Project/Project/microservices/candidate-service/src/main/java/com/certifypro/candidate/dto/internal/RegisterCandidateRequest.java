package com.certifypro.candidate.dto.internal;

import java.time.LocalDate;

/**
 * Payload auth-service sends to POST /api/candidates/internal/register to create
 * the candidate profile paired with a newly registered user. Raw DTO (no envelope).
 */
public record RegisterCandidateRequest(
        Long userId,
        String name,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String gender,
        String highestQualification,
        String professionalExperience,
        String employerName
) {
}
