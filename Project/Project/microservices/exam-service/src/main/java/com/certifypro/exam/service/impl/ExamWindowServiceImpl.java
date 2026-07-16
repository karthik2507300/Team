package com.certifypro.exam.service.impl;

import com.certifypro.exam.common.ExamWindowStatus;
import com.certifypro.exam.dto.request.CreateExamWindowRequest;
import com.certifypro.exam.dto.request.UpdateExamWindowRequest;
import com.certifypro.exam.dto.response.ExamWindowResponse;
import com.certifypro.exam.dto.response.PageResponse;
import com.certifypro.exam.entity.ExamWindow;
import com.certifypro.exam.exception.NotFoundException;
import com.certifypro.exam.repository.ExamWindowRepository;
import com.certifypro.exam.service.ExamWindowService;
import com.certifypro.exam.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExamWindowServiceImpl implements ExamWindowService {

    private final ExamWindowRepository examWindowRepository;

    public ExamWindowServiceImpl(ExamWindowRepository examWindowRepository) {
        this.examWindowRepository = examWindowRepository;
    }

    @Override
    @Transactional
    public ExamWindowResponse create(CreateExamWindowRequest req) {
        // SIMPLIFIED: monolith verified the CertificationProgram existed via the
        // program repository. programId is now accepted as a plain cross-service id.
        ExamWindow w = ExamWindow.builder()
                .programId(req.programId())
                .examName(req.examName())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .registrationDeadline(req.registrationDeadline())
                .resultDate(req.resultDate())
                .status(ExamWindowStatus.Upcoming)
                .build();
        return ExamWindowResponse.from(examWindowRepository.save(w));
    }

    @Override
    public PageResponse<ExamWindowResponse> list(Long programId, int page, int limit) {
        if (programId != null) {
            return PageResponse.from(examWindowRepository
                    .findByProgramId(programId, PageUtil.of(page, limit))
                    .map(ExamWindowResponse::from));
        }
        return PageResponse.from(examWindowRepository.findAll(PageUtil.of(page, limit))
                .map(ExamWindowResponse::from));
    }

    @Override
    public ExamWindowResponse getById(Long id) {
        return ExamWindowResponse.from(findWindow(id));
    }

    @Override
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
