package com.certifypro.dto.response;

import com.certifypro.model.Certificate;

import java.time.LocalDate;

public record CertificateResponse(
        Long certificateId,
        Long candidateId,
        Long programId,
        String certificateNumber,
        LocalDate issuedDate,
        LocalDate validUntil,
        Long issuedById,
        String status
) {
    public static CertificateResponse from(Certificate c) {
        return new CertificateResponse(
                c.getCertificateId(), c.getCandidateId(), c.getProgramId(), c.getCertificateNumber(),
                c.getIssuedDate(), c.getValidUntil(), c.getIssuedById(),
                c.getStatus() == null ? null : c.getStatus().name());
    }
}
