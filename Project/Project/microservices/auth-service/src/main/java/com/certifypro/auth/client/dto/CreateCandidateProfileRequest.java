package com.certifypro.auth.client.dto;

import java.time.LocalDate;

/** Payload sent to candidate-service to create the candidate profile at registration. */
public record CreateCandidateProfileRequest(
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
