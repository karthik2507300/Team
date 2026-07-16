package com.certifypro.service;

import com.certifypro.dto.request.CreateExamWindowRequest;
import com.certifypro.dto.request.UpdateExamWindowRequest;
import com.certifypro.dto.response.ExamWindowResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.ExamWindow;
import com.certifypro.model.enums.ExamWindowStatus;
import com.certifypro.repository.CertificationProgramRepository;
import com.certifypro.repository.ExamWindowRepository;
import com.certifypro.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamWindowService {

    private final ExamWindowRepository examWindowRepository;
    private final CertificationProgramRepository programRepository;

    public ExamWindowService(ExamWindowRepository examWindowRepository,
                             CertificationProgramRepository programRepository) {
        this.examWindowRepository = examWindowRepository;
        this.programRepository = programRepository;
    }

    @Transactional
    public ExamWindowResponse create(CreateExamWindowRequest req) {
        if (!programRepository.existsById(req.programId())) {
            throw NotFoundException.of("CertificationProgram", req.programId());
        }
        ExamWindow w = new ExamWindow();
        w.setProgramId(req.programId());
        w.setExamName(req.examName());
        w.setStartDate(req.startDate());
        w.setEndDate(req.endDate());
        w.setRegistrationDeadline(req.registrationDeadline());
        w.setResultDate(req.resultDate());
        w.setStatus(ExamWindowStatus.Upcoming);
        return ExamWindowResponse.from(examWindowRepository.save(w));
    }

    public PageResponse<ExamWindowResponse> list(Long programId, int page, int limit) {
        if (programId != null) {
            return PageResponse.from(examWindowRepository
                    .findByProgramId(programId, PageUtil.of(page, limit))
                    .map(ExamWindowResponse::from));
        }
        return PageResponse.from(examWindowRepository.findAll(PageUtil.of(page, limit))
                .map(ExamWindowResponse::from));
    }

    public ExamWindowResponse getById(Long id) {
        return ExamWindowResponse.from(findWindow(id));
    }

    @Transactional
    public ExamWindowResponse update(Long id, UpdateExamWindowRequest req) {
        ExamWindow w = findWindow(id);
        if (req.examName() != null) w.setExamName(req.examName());
        if (req.startDate() != null) w.setStartDate(req.startDate());
        if (req.endDate() != null) w.setEndDate(req.endDate());
        if (req.registrationDeadline() != null) w.setRegistrationDeadline(req.registrationDeadline());
        if (req.resultDate() != null) w.setResultDate(req.resultDate());
        if (req.status() != null) w.setStatus(parseStatus(req.status()));
        return ExamWindowResponse.from(examWindowRepository.save(w));
    }

    private ExamWindow findWindow(Long id) {
        return examWindowRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("ExamWindow", id));
    }

    private ExamWindowStatus parseStatus(String value) {
        try {
            return ExamWindowStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + value
                    + " (allowed: Upcoming, Open, Closed, ResultsPublished)");
        }
    }
}
