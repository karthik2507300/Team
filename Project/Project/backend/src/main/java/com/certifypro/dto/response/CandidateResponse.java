package com.certifypro.dto.response;

import com.certifypro.model.Candidate;

import java.time.LocalDate;

public record CandidateResponse(
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
    public static CandidateResponse from(Candidate c) {
        return new CandidateResponse(
                c.getCandidateId(), c.getUserId(), c.getName(), c.getDateOfBirth(),
                c.getGender(), c.getEmail(), c.getPhone(), c.getAddress(),
                c.getHighestQualification(), c.getProfessionalExperience(),
                c.getEmployerName(), c.getStatus() == null ? null : c.getStatus().name());
    }
}
