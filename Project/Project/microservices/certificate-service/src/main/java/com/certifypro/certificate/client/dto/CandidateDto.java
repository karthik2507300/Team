package com.certifypro.certificate.client.dto;

import java.time.LocalDate;

/**
 * Local copy of candidate-service's candidate view (raw DTO from
 * GET /api/candidates/internal/{candidateId}). Only candidateId + userId are
 * needed here (to notify the owning user), the rest mirror the source shape.
 */
public record CandidateDto(
        Long candidateId,
        Long userId,
        String name,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phone,
        String address,
        String highestQualification,
        String professionalExperience,
        String employerName,
        String status
) {
}
