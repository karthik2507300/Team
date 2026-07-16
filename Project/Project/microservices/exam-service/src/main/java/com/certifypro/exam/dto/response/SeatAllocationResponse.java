package com.certifypro.exam.dto.response;

import com.certifypro.exam.entity.SeatAllocation;

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
                s.getAllocationId(),
                s.getCandidateId(),
                s.getExamWindow() == null ? null : s.getExamWindow().getWindowId(),
                s.getTestCentre() == null ? null : s.getTestCentre().getCentreId(),
                s.getRoomNumber(), s.getSeatNumber(), s.getHallTicketNumber(),
                s.getStatus() == null ? null : s.getStatus().name());
    }
}
