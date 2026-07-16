package com.certifypro.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateScriptAllocationRequest(
        @NotNull Long allocationId,
        @NotNull Long evaluatorId,
        @NotNull Long paperId
) {
}
