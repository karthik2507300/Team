package com.certifypro.result.client.dto;

import java.time.LocalDate;

/**
 * Local copy of exam-service's exam window view (raw DTO from
 * GET /api/exam-windows/internal/{windowId}). Used to resolve the programId a
 * window belongs to during result compute. Extra fields mirror the source shape;
 * unknown JSON properties are ignored by Jackson.
 */
public record ExamWindowDto(
        Long windowId,
        Long programId,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {
}
