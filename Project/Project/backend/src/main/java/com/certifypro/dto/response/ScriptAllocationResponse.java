package com.certifypro.dto.response;

import com.certifypro.model.ScriptAllocation;

import java.time.LocalDate;

public record ScriptAllocationResponse(
        Long scriptId,
        Long allocationId,
        Long evaluatorId,
        Long paperId,
        LocalDate allocationDate,
        String status
) {
    public static ScriptAllocationResponse from(ScriptAllocation s) {
        return new ScriptAllocationResponse(
                s.getScriptId(), s.getAllocationId(), s.getEvaluatorId(), s.getPaperId(),
                s.getAllocationDate(), s.getStatus() == null ? null : s.getStatus().name());
    }
}
