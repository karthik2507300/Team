package com.certifypro.result.client.dto;

/**
 * Local copy of exam-service's seat allocation view (raw DTO from
 * GET /api/seat-allocations/internal/{allocationId}). Used to resolve which
 * candidate a script allocation belongs to.
 */
public record SeatAllocationDto(
        Long allocationId,
        Long candidateId,
        Long windowId,
        Long centreId,
        String hallTicketNumber
) {
}
