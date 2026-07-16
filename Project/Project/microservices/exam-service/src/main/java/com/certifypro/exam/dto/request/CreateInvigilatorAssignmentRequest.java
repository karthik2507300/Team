package com.certifypro.exam.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateInvigilatorAssignmentRequest(
        @NotNull Long windowId,
        @NotNull Long centreId,
        @NotNull Long userId,
        String roomNumber
) {
}
