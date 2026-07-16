package com.certifypro.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Candidate self-registration. Creates a User (role Candidate) in this service,
 * then a Candidate profile in candidate-service via Feign.
 */
public record RegisterRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        LocalDate dateOfBirth,
        String gender,
        String highestQualification,
        String professionalExperience,
        String employerName
) {
}
