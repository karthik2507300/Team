package com.certifypro.exam.dto.response;

import com.certifypro.exam.entity.InvigilatorAssignment;

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
                a.getAssignmentId(),
                a.getExamWindow() == null ? null : a.getExamWindow().getWindowId(),
                a.getTestCentre() == null ? null : a.getTestCentre().getCentreId(),
                a.getUserId(),
                a.getRoomNumber(), a.getStatus() == null ? null : a.getStatus().name());
    }
}
