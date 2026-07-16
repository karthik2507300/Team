package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;

/** status must be one of: Active, Inactive, Suspended */
public record UpdateUserStatusRequest(
        @NotBlank String status
) {
}
