package com.certifypro.dto.response;

import com.certifypro.model.GradingScale;

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
                g.getGradeId(), g.getProgramId(), g.getGradeLetter(),
                g.getMinPercentage(), g.getMaxPercentage(), g.getIsPassing());
    }
}
