package com.certifypro.notification.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Internal/admin notification trigger. Provide either userId (single user)
 * or role (fan-out to all users with that role).
 * category: Registration | Exam | Result | Certificate | Renewal
 */
public record CreateNotificationRequest(
        Long userId,
        String role,
        @NotBlank String message,
        @NotBlank String category
) {
}
