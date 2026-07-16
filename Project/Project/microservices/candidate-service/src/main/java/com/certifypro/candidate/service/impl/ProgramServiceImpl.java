package com.certifypro.candidate.service.impl;

import com.certifypro.candidate.common.ProgramLevel;
import com.certifypro.candidate.common.ProgramStatus;
import com.certifypro.candidate.dto.internal.GradingScaleDto;
import com.certifypro.candidate.dto.internal.ProgramDto;
import com.certifypro.candidate.dto.request.CreateProgramRequest;
import com.certifypro.candidate.dto.request.GradingScaleRequest;
import com.certifypro.candidate.dto.request.UpdateProgramRequest;
import com.certifypro.candidate.dto.response.GradingScaleResponse;
import com.certifypro.candidate.dto.response.PageResponse;
import com.certifypro.candidate.dto.response.ProgramResponse;
import com.certifypro.candidate.entity.CertificationProgram;
import com.certifypro.candidate.entity.GradingScale;
import com.certifypro.candidate.exception.NotFoundException;
import com.certifypro.candidate.repository.CertificationProgramRepository;
import com.certifypro.candidate.repository.GradingScaleRepository;
import com.certifypro.candidate.service.ProgramService;
import com.certifypro.candidate.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgramServiceImpl implements ProgramService {

    private final CertificationProgramRepository programRepository;
    private final GradingScaleRepository gradingScaleRepository;

    public ProgramServiceImpl(CertificationProgramRepository programRepository,
                              GradingScaleRepository gradingScaleRepository) {
        this.programRepository = programRepository;
        this.gradingScaleRepository = gradingScaleRepository;
    }

    @Override
    public PageResponse<ProgramResponse> listActive(int page, int limit) {
        return PageResponse.from(
                programRepository.findByStatus(ProgramStatus.Active, PageUtil.of(page, limit))
                        .map(ProgramResponse::from));
    }

    @Override
    @Transactional
    public ProgramResponse create(CreateProgramRequest req) {
        CertificationProgram p = CertificationProgram.builder()
                .programName(req.programName())
                .body(req.body())
                .level(parseLevel(req.level()))
                .eligibilityCriteria(req.eligibilityCriteria())
                .examFee(req.examFee())
                .validityYears(req.validityYears())
                .maxAttempts(req.maxAttempts())
                .status(ProgramStatus.Active)
                .build();
        return ProgramResponse.from(programRepository.save(p));
    }

    @Override
    @Transactional
    public ProgramResponse update(Long id, UpdateProgramRequest req) {
        CertificationProgram p = findProgram(id);
        if (req.programName() != null) p.setProgramName(req.programName());
        if (req.body() != null) p.setBody(req.body());
        if (req.level() != null) p.setLevel(parseLevel(req.level()));
        if (req.eligibilityCriteria() != null) p.setEligibilityCriteria(req.eligibilityCriteria());
        if (req.examFee() != null) p.setExamFee(req.examFee());
        if (req.validityYears() != null) p.setValidityYears(req.validityYears());
        if (req.maxAttempts() != null) p.setMaxAttempts(req.maxAttempts());
        if (req.status() != null) p.setStatus(parseStatus(req.status()));
        return ProgramResponse.from(programRepository.save(p));
    }

    @Override
    @Transactional
    public List<GradingScaleResponse> setGradingScale(Long programId, GradingScaleRequest req) {
        CertificationProgram program = findProgram(programId);

        // Replace any existing scale for this program.
        List<GradingScale> existing = gradingScaleRepository.findByProgram_ProgramId(program.getProgramId());
        if (!existing.isEmpty()) {
            gradingScaleRepository.deleteAll(existing);
        }

        List<GradingScale> saved = req.bands().stream().map(b -> {
            GradingScale g = GradingScale.builder()
                    .program(program)
                    .gradeLetter(b.gradeLetter())
                    .minPercentage(b.minPercentage())
                    .maxPercentage(b.maxPercentage())
                    .isPassing(b.isPassing())
                    .build();
            return gradingScaleRepository.save(g);
        }).toList();

        return saved.stream().map(GradingScaleResponse::from).toList();
    }

    @Override
    public List<GradingScaleResponse> getGradingScale(Long programId) {
        findProgram(programId);
        return gradingScaleRepository.findByProgram_ProgramId(programId).stream()
                .map(GradingScaleResponse::from).toList();
    }

    @Override
    public ProgramDto getProgramDto(Long programId) {
        return ProgramDto.from(findProgram(programId));
    }

    @Override
    public List<GradingScaleDto> getGradingScaleDto(Long programId) {
        findProgram(programId);
        return gradingScaleRepository.findByProgram_ProgramId(programId).stream()
                .map(GradingScaleDto::from).toList();
    }

    private CertificationProgram findProgram(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("CertificationProgram", id));
    }

    private ProgramLevel parseLevel(String value) {
        try {
            return ProgramLevel.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid level: " + value
                    + " (allowed: Foundation, Associate, Professional, Fellow)");
        }
    }

    private ProgramStatus parseStatus(String value) {
        try {
            return ProgramStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + value
                    + " (allowed: Active, Discontinued)");
        }
    }
}
