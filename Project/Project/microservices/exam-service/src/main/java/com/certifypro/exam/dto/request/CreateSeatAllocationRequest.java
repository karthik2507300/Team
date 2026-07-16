package com.certifypro.exam.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * Allocate a candidate to a seat for an exam window at a centre.
 * candidateId is optional: candidates allocate for themselves (derived from token);
 * Admin/CentreAdmin may allocate on behalf of a candidate by supplying it.
 */
public record CreateSeatAllocationRequest(
        @NotNull Long windowId,
        @NotNull Long centreId,
        Long candidateId,
        String roomNumber,
        String seatNumber
) {
}
