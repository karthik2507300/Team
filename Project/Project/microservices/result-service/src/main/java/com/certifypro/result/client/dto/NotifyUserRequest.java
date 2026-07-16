package com.certifypro.result.client.dto;

/**
 * Payload sent to notification-service POST /api/notifications/internal to
 * create a single Unread notification for one user.
 * category: Registration | Exam | Result | Certificate | Renewal
 */
public record NotifyUserRequest(
        Long userId,
        String message,
        String category
) {
}
