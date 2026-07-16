package com.certifypro.dto.request;

import jakarta.validation.constraints.NotNull;

/** Certification Officer manually issues a certificate for a candidate + program. */
public record IssueCertificateRequest(
        @NotNull Long candidateId,
        @NotNull Long programId
) {
}
