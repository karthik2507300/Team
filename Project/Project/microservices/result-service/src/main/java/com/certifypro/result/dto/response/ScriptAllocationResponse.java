package com.certifypro.result.dto.response;

import com.certifypro.result.entity.ScriptAllocation;

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
