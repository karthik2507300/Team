package com.certifypro.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Generate an analytics report.
 * scope: Program | Window | Centre | Period
 * The relevant id is supplied via programId / windowId / centreId depending on scope.
 */
public record GenerateReportRequest(
        @NotBlank String scope,
        Long programId,
        Long windowId,
        Long centreId
) {
}
