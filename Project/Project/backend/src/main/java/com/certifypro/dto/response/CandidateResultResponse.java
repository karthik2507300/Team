package com.certifypro.dto.response;

import com.certifypro.model.CandidateResult;

import java.time.LocalDate;

public record CandidateResultResponse(
        Long resultId,
        Long candidateId,
        Long windowId,
        Long programId,
        Integer totalMarks,
        Integer marksObtained,
        Float percentage,
        String grade,
        String outcome,
        LocalDate publishedDate,
        String status
) {
    public static CandidateResultResponse from(CandidateResult r) {
        return new CandidateResultResponse(
                r.getResultId(), r.getCandidateId(), r.getWindowId(), r.getProgramId(),
                r.getTotalMarks(), r.getMarksObtained(), r.getPercentage(), r.getGrade(),
                r.getOutcome() == null ? null : r.getOutcome().name(),
                r.getPublishedDate(), r.getStatus() == null ? null : r.getStatus().name());
    }
}
