package com.certifypro.candidate.service;

import com.certifypro.candidate.dto.internal.GradingScaleDto;
import com.certifypro.candidate.dto.internal.ProgramDto;
import com.certifypro.candidate.dto.request.CreateProgramRequest;
import com.certifypro.candidate.dto.request.GradingScaleRequest;
import com.certifypro.candidate.dto.request.UpdateProgramRequest;
import com.certifypro.candidate.dto.response.GradingScaleResponse;
import com.certifypro.candidate.dto.response.PageResponse;
import com.certifypro.candidate.dto.response.ProgramResponse;

import java.util.List;

/** Certification program + grading scale use cases. */
public interface ProgramService {

    PageResponse<ProgramResponse> listActive(int page, int limit);

    ProgramResponse create(CreateProgramRequest req);

    ProgramResponse update(Long id, UpdateProgramRequest req);

    List<GradingScaleResponse> setGradingScale(Long programId, GradingScaleRequest req);

    List<GradingScaleResponse> getGradingScale(Long programId);

    /** Service-to-service: compact program view. */
    ProgramDto getProgramDto(Long programId);

    /** Service-to-service: grading bands for a program. */
    List<GradingScaleDto> getGradingScaleDto(Long programId);
}
