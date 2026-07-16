package com.certifypro.exam.dto.response;

import com.certifypro.exam.entity.ExamWindow;

import java.time.LocalDate;

public record ExamWindowResponse(
        Long windowId,
        Long programId,
        String examName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        LocalDate resultDate,
        String status
) {
    public static ExamWindowResponse from(ExamWindow w) {
        return new ExamWindowResponse(
                w.getWindowId(), w.getProgramId(), w.getExamName(), w.getStartDate(),
                w.getEndDate(), w.getRegistrationDeadline(), w.getResultDate(),
                w.getStatus() == null ? null : w.getStatus().name());
    }
}
