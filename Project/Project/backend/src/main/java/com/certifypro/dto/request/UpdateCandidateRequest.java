package com.certifypro.dto.request;

import java.time.LocalDate;

/** Candidate edits own profile. Null fields are left unchanged. */
public record UpdateCandidateRequest(
        String name,
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
