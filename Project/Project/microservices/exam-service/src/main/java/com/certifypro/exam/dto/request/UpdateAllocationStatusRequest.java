package com.certifypro.exam.dto.request;

import jakarta.validation.constraints.NotBlank;

/** status must be one of: Allocated, Confirmed, Cancelled, NoShow */
public record UpdateAllocationStatusRequest(
        @NotBlank String status
) {
}
