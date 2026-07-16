package com.certifypro.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Service-to-service payload to notify a single user.
 * category: Registration | Exam | Result | Certificate | Renewal
 */
public record InternalNotifyUserRequest(
        @NotNull Long userId,
        @NotBlank String message,
        @NotBlank String category
) {
}
