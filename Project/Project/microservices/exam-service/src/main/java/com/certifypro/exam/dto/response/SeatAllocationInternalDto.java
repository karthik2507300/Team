package com.certifypro.exam.dto.response;

import com.certifypro.exam.entity.SeatAllocation;

/** Raw service-to-service projection of a seat allocation (no ApiResponse envelope). */
public record SeatAllocationInternalDto(
        Long allocationId,
        Long candidateId,
        Long windowId,
        Long centreId,
        String hallTicketNumber
) {
    public static SeatAllocationInternalDto from(SeatAllocation s) {
        return new SeatAllocationInternalDto(
                s.getAllocationId(),
                s.getCandidateId(),
                s.getExamWindow() == null ? null : s.getExamWindow().getWindowId(),
                s.getTestCentre() == null ? null : s.getTestCentre().getCentreId(),
                s.getHallTicketNumber());
    }
}
