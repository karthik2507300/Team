package com.certifypro.result.client.dto;

import java.time.LocalDate;

/**
 * Local copy of certificate-service's certificate view (raw DTO returned by
 * POST /api/certificates/internal/issue). Captured for logging only; result
 * publish does not depend on the returned certificate.
 */
public record CertificateDto(
        Long certificateId,
        Long candidateId,
        Long programId,
        String certificateNumber,
        LocalDate issuedDate,
        LocalDate validUntil,
        Long issuedById,
        String status
) {
}
