package com.certifypro.result.client.dto;

/**
 * Payload sent to notification-service POST /api/notifications/internal/by-role
 * to fan a notification out to every user holding the given role (e.g. flagging
 * a script for moderation to all ExamControllers).
 * category: Registration | Exam | Result | Certificate | Renewal
 */
public record NotifyRoleRequest(
        String role,
        String message,
        String category
) {
}
