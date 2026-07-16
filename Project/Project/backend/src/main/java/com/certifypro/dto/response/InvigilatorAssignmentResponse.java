package com.certifypro.dto.response;

import com.certifypro.model.InvigilatorAssignment;

public record InvigilatorAssignmentResponse(
        Long assignmentId,
        Long windowId,
        Long centreId,
        Long userId,
        String roomNumber,
        String status
) {
    public static InvigilatorAssignmentResponse from(InvigilatorAssignment a) {
        return new InvigilatorAssignmentResponse(
                a.getAssignmentId(), a.getWindowId(), a.getCentreId(), a.getUserId(),
                a.getRoomNumber(), a.getStatus() == null ? null : a.getStatus().name());
    }
}
