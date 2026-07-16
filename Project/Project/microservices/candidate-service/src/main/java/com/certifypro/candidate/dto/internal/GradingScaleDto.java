package com.certifypro.candidate.dto.internal;

import com.certifypro.candidate.entity.GradingScale;

/** Grade band view result-service uses to grade a candidate's percentage. */
public record GradingScaleDto(
        String gradeLetter,
        Integer minPercentage,
        Integer maxPercentage,
        Boolean isPassing
) {
    public static GradingScaleDto from(GradingScale g) {
        return new GradingScaleDto(
                g.getGradeLetter(), g.getMinPercentage(), g.getMaxPercentage(), g.getIsPassing());
    }
}
