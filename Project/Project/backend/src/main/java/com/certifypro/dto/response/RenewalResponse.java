package com.certifypro.dto.response;

import com.certifypro.model.RenewalApplication;

import java.time.LocalDate;

public record RenewalResponse(
        Long renewalId,
        Long certificateId,
        Long candidateId,
        Integer cpdPointsSubmitted,
        LocalDate applicationDate,
        Long reviewedById,
        LocalDate newValidUntil,
        String status
) {
    public static RenewalResponse from(RenewalApplication r) {
        return new RenewalResponse(
                r.getRenewalId(), r.getCertificateId(), r.getCandidateId(),
                r.getCpdPointsSubmitted(), r.getApplicationDate(), r.getReviewedById(),
                r.getNewValidUntil(), r.getStatus() == null ? null : r.getStatus().name());
    }
}
