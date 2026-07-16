package com.certifypro.candidate.dto.response;

import com.certifypro.candidate.entity.GradingScale;

public record GradingScaleResponse(
        Long gradeId,
        Long programId,
        String gradeLetter,
        Integer minPercentage,
        Integer maxPercentage,
        Boolean isPassing
) {
    public static GradingScaleResponse from(GradingScale g) {
        return new GradingScaleResponse(
                g.getGradeId(),
                g.getProgram() == null ? null : g.getProgram().getProgramId(),
                g.getGradeLetter(),
                g.getMinPercentage(), g.getMaxPercentage(), g.getIsPassing());
    }
}
