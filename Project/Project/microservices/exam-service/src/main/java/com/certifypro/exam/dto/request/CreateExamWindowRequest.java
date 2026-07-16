package com.certifypro.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateExamWindowRequest(
        @NotNull Long programId,
        @NotBlank String examName,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate registrationDeadline,
        LocalDate resultDate
) {
}
