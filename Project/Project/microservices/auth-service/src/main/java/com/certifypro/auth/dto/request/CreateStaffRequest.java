package com.certifypro.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Admin creates a staff user. Role must be a staff role (not Candidate).
 * If password is omitted, a default (Password@123) is assigned.
 */
public record CreateStaffRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank String role,
        String password
) {
}
