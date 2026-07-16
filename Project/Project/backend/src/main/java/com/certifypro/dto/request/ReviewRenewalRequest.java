package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;

/** decision: Approved or Rejected. On Approval the certificate validity is extended. */
public record ReviewRenewalRequest(
        @NotBlank String decision,
        Integer extendYears
) {
}
