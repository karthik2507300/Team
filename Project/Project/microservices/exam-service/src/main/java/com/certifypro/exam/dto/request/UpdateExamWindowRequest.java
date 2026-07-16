package com.certifypro.exam.dto.request;

import java.time.LocalDate;

/** Edit/open/close an exam window. status may be Upcoming/Open/Closed/ResultsPublished. */
public record UpdateExamWindowRequest(
        String examName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        LocalDate resultDate,
        String status
) {
}
