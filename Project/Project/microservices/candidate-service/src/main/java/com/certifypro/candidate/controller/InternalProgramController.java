package com.certifypro.candidate.controller;

import com.certifypro.candidate.dto.internal.GradingScaleDto;
import com.certifypro.candidate.dto.internal.ProgramDto;
import com.certifypro.candidate.service.ProgramService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Service-to-service program lookups (raw DTOs). Used by exam/question/result
 * services. Permitted without a role via /api/**&#47;internal/**.
 */
@RestController
@RequestMapping("/api/programs/internal")
public class InternalProgramController {

    private final ProgramService programService;

    public InternalProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping("/{programId}")
    public ProgramDto getProgram(@PathVariable Long programId) {
        return programService.getProgramDto(programId);
    }

    @GetMapping("/{programId}/grading-scale")
    public List<GradingScaleDto> getGradingScale(@PathVariable Long programId) {
        return programService.getGradingScaleDto(programId);
    }
}
