package com.certifypro.result.client.dto;

/**
 * Payload sent to certificate-service POST /api/certificates/internal/issue to
 * auto-issue a certificate when a Pass result is published.
 */
public record IssueCertificateRequest(
        Long candidateId,
        Long programId
) {
}
