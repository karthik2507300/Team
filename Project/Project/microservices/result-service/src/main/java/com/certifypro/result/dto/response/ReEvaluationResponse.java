package com.certifypro.result.dto.response;

import com.certifypro.result.entity.ReEvaluationRequest;

import java.time.LocalDate;

public record ReEvaluationResponse(
        Long requestId,
        Long resultId,
        Long candidateId,
        LocalDate requestDate,
        String reason,
        String status
) {
    public static ReEvaluationResponse from(ReEvaluationRequest r) {
        return new ReEvaluationResponse(
                r.getRequestId(),
                r.getResult() == null ? null : r.getResult().getResultId(),
                r.getCandidateId(),
                r.getRequestDate(), r.getReason(),
                r.getStatus() == null ? null : r.getStatus().name());
    }
}
