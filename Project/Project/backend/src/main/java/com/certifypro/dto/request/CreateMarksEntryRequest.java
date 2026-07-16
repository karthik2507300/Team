package com.certifypro.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateMarksEntryRequest(
        @NotNull Long scriptId,
        @NotNull @PositiveOrZero Integer marksAwarded
) {
}
