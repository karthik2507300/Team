package com.certifypro.service;

import com.certifypro.dto.request.CreateProgramRequest;
import com.certifypro.dto.request.GradingScaleRequest;
import com.certifypro.dto.request.UpdateProgramRequest;
import com.certifypro.dto.response.GradingScaleResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.ProgramResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.CertificationProgram;
import com.certifypro.model.GradingScale;
import com.certifypro.model.enums.ProgramLevel;
import com.certifypro.model.enums.ProgramStatus;
import com.certifypro.repository.CertificationProgramRepository;
import com.certifypro.repository.GradingScaleRepository;
import com.certifypro.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgramService {

    private final CertificationProgramRepository programRepository;
    private final GradingScaleRepository gradingScaleRepository;

    public ProgramService(CertificationProgramRepository programRepository,
                          GradingScaleRepository gradingScaleRepository) {
        this.programRepository = programRepository;
        this.gradingScaleRepository = gradingScaleRepository;
    }

    /** Lists active certification programs (paginated). */
    public PageResponse<ProgramResponse> listActive(int page, int limit) {
        return PageResponse.from(
                programRepository.findByStatus(ProgramStatus.Active, PageUtil.of(page, limit))
                        .map(ProgramResponse::from));
    }

    @Transactional
    public ProgramResponse create(CreateProgramRequest req) {
        CertificationProgram p = new CertificationProgram();
        p.setProgramName(req.programName());
        p.setBody(req.body());
        p.setLevel(parseLevel(req.level()));
        p.setEligibilityCriteria(req.eligibilityCriteria());
        p.setExamFee(req.examFee());
        p.setValidityYears(req.validityYears());
        p.setMaxAttempts(req.maxAttempts());
        p.setStatus(ProgramStatus.Active);
        return ProgramResponse.from(programRepository.save(p));
    }

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

    @Transactional
    public List<GradingScaleResponse> setGradingScale(Long programId, GradingScaleRequest req) {
        CertificationProgram program = findProgram(programId);

        // Replace any existing scale for this program.
        List<GradingScale> existing = gradingScaleRepository.findByProgramId(program.getProgramId());
        if (!existing.isEmpty()) {
            gradingScaleRepository.deleteAll(existing);
        }

        List<GradingScale> saved = req.bands().stream().map(b -> {
            GradingScale g = new GradingScale();
            g.setProgramId(program.getProgramId());
            g.setGradeLetter(b.gradeLetter());
            g.setMinPercentage(b.minPercentage());
            g.setMaxPercentage(b.maxPercentage());
            g.setIsPassing(b.isPassing());
            return gradingScaleRepository.save(g);
        }).toList();

        return saved.stream().map(GradingScaleResponse::from).toList();
    }

    public List<GradingScaleResponse> getGradingScale(Long programId) {
        findProgram(programId);
        return gradingScaleRepository.findByProgramId(programId).stream()
                .map(GradingScaleResponse::from).toList();
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
