package com.certifypro.candidate.dto.request;

import jakarta.validation.constraints.NotNull;

/** Candidate enrols in a program. Candidate is resolved from the auth token. */
public record CreateEnrolmentRequest(
        @NotNull Long programId
) {
}
