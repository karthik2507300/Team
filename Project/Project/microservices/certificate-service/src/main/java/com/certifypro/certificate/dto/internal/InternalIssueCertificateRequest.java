package com.certifypro.certificate.dto.internal;

import jakarta.validation.constraints.NotNull;

/**
 * Service-to-service payload to auto-issue a certificate (called by result-service
 * when a Pass result is published).
 */
public record InternalIssueCertificateRequest(
        @NotNull Long candidateId,
        @NotNull Long programId
) {
}
