package com.certifypro.dto.response;

import com.certifypro.model.SeatAllocation;

public record SeatAllocationResponse(
        Long allocationId,
        Long candidateId,
        Long windowId,
        Long centreId,
        String roomNumber,
        String seatNumber,
        String hallTicketNumber,
        String status
) {
    public static SeatAllocationResponse from(SeatAllocation s) {
        return new SeatAllocationResponse(
                s.getAllocationId(), s.getCandidateId(), s.getWindowId(), s.getCentreId(),
                s.getRoomNumber(), s.getSeatNumber(), s.getHallTicketNumber(),
                s.getStatus() == null ? null : s.getStatus().name());
    }
}
