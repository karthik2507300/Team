package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;

/** eligibilityStatus must be one of: Eligible, Ineligible, PendingVerification */
public record UpdateEligibilityRequest(
        @NotBlank String eligibilityStatus
) {
}
