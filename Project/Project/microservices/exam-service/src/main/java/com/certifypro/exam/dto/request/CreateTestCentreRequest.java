package com.certifypro.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateTestCentreRequest(
        @NotBlank String centreName,
        String city,
        String address,
        @PositiveOrZero Integer capacity,
        String contactPerson
) {
}
