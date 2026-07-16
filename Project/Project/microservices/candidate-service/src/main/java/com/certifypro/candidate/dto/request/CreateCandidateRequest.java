package com.certifypro.candidate.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/** Creates the Candidate profile for the currently authenticated candidate user. */
public record CreateCandidateRequest(
        @NotBlank String name,
        LocalDate dateOfBirth,
        String gender,
        String email,
        String phone,
        String address,
        String highestQualification,
        String professionalExperience,
        String employerName
) {
}
