package com.certifypro.dto.response;

import com.certifypro.model.MarksEntry;

import java.time.LocalDate;

public record MarksEntryResponse(
        Long marksId,
        Long scriptId,
        Long evaluatorId,
        Integer marksAwarded,
        LocalDate entryDate,
        Long verifiedById,
        String status
) {
    public static MarksEntryResponse from(MarksEntry m) {
        return new MarksEntryResponse(
                m.getMarksId(), m.getScriptId(), m.getEvaluatorId(), m.getMarksAwarded(),
                m.getEntryDate(), m.getVerifiedById(),
                m.getStatus() == null ? null : m.getStatus().name());
    }
}
