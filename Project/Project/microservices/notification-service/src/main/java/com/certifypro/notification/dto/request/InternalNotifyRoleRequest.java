package com.certifypro.notification.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Service-to-service payload to fan a notification out to every user of a role.
 * category: Registration | Exam | Result | Certificate | Renewal
 */
public record InternalNotifyRoleRequest(
        @NotBlank String role,
        @NotBlank String message,
        @NotBlank String category
) {
}
