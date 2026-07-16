package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;

/** status: Draft -> Finalised -> Distributed -> Archived (forward only) */
public record UpdatePaperStatusRequest(
        @NotBlank String status
) {
}
