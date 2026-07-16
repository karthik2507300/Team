package com.certifypro.dto.response;

import com.certifypro.model.ProgramEnrolment;

import java.time.LocalDate;

public record EnrolmentResponse(
        Long enrolmentId,
        Long candidateId,
        Long programId,
        LocalDate enrolmentDate,
        String eligibilityStatus,
        Integer attemptsUsed,
        Integer maxAttempts,
        String status
) {
    public static EnrolmentResponse from(ProgramEnrolment e) {
        return new EnrolmentResponse(
                e.getEnrolmentId(), e.getCandidateId(), e.getProgramId(), e.getEnrolmentDate(),
                e.getEligibilityStatus() == null ? null : e.getEligibilityStatus().name(),
                e.getAttemptsUsed(), e.getMaxAttempts(),
                e.getStatus() == null ? null : e.getStatus().name());
    }
}
