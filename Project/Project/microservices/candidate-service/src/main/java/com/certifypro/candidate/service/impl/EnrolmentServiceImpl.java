package com.certifypro.candidate.service.impl;

import com.certifypro.candidate.common.EligibilityStatus;
import com.certifypro.candidate.common.EnrolmentStatus;
import com.certifypro.candidate.common.ProgramStatus;
import com.certifypro.candidate.dto.request.CreateEnrolmentRequest;
import com.certifypro.candidate.dto.request.UpdateEligibilityRequest;
import com.certifypro.candidate.dto.response.EnrolmentResponse;
import com.certifypro.candidate.dto.response.PageResponse;
import com.certifypro.candidate.entity.Candidate;
import com.certifypro.candidate.entity.CertificationProgram;
import com.certifypro.candidate.entity.ProgramEnrolment;
import com.certifypro.candidate.exception.BusinessException;
import com.certifypro.candidate.exception.NotFoundException;
import com.certifypro.candidate.repository.CandidateRepository;
import com.certifypro.candidate.repository.CertificationProgramRepository;
import com.certifypro.candidate.repository.ProgramEnrolmentRepository;
import com.certifypro.candidate.security.SecurityUtil;
import com.certifypro.candidate.service.EnrolmentService;
import com.certifypro.candidate.util.PageUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class EnrolmentServiceImpl implements EnrolmentService {

    private final ProgramEnrolmentRepository enrolmentRepository;
    private final CandidateRepository candidateRepository;
    private final CertificationProgramRepository programRepository;

    public EnrolmentServiceImpl(ProgramEnrolmentRepository enrolmentRepository,
                                CandidateRepository candidateRepository,
                                CertificationProgramRepository programRepository) {
        this.enrolmentRepository = enrolmentRepository;
        this.candidateRepository = candidateRepository;
        this.programRepository = programRepository;
    }

    @Override
    @Transactional
    public EnrolmentResponse create(CreateEnrolmentRequest req) {
        Candidate candidate = candidateRepository.findByUserId(SecurityUtil.currentUserId())
                .orElseThrow(() -> new BusinessException("Complete your candidate profile before enrolling"));

        CertificationProgram program = programRepository.findById(req.programId())
                .orElseThrow(() -> NotFoundException.of("CertificationProgram", req.programId()));
        if (program.getStatus() != ProgramStatus.Active) {
            throw new BusinessException("This program is not open for enrolment");
        }

        enrolmentRepository.findByCandidateIdAndProgram_ProgramId(candidate.getCandidateId(), program.getProgramId())
                .ifPresent(e -> {
                    throw new BusinessException("You are already enrolled in this program");
                });

        ProgramEnrolment e = ProgramEnrolment.builder()
                .candidateId(candidate.getCandidateId())
                .program(program)
                .enrolmentDate(LocalDate.now())
                .eligibilityStatus(EligibilityStatus.PendingVerification)
                .attemptsUsed(0)
                .maxAttempts(program.getMaxAttempts())
                .status(EnrolmentStatus.Active)
                .build();
        return EnrolmentResponse.from(enrolmentRepository.save(e));
    }

    @Override
    public PageResponse<EnrolmentResponse> list(String eligibilityStatus, int page, int limit) {
        var pageable = PageUtil.of(page, limit);
        if (eligibilityStatus != null) {
            return PageResponse.from(
                    enrolmentRepository.findByEligibilityStatus(
                            parseEligibility(eligibilityStatus), pageable).map(EnrolmentResponse::from));
        }
        return PageResponse.from(
                enrolmentRepository.findAll(pageable).map(EnrolmentResponse::from));
    }

    @Override
    public EnrolmentResponse getById(Long id) {
        ProgramEnrolment e = findEnrolment(id);
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            Candidate candidate = candidateRepository.findByUserId(SecurityUtil.currentUserId()).orElse(null);
            if (candidate == null || !Objects.equals(candidate.getCandidateId(), e.getCandidateId())) {
                throw new AccessDeniedException("You can only view your own enrolments");
            }
        }
        return EnrolmentResponse.from(e);
    }

    @Override
    @Transactional
    public EnrolmentResponse updateEligibility(Long id, UpdateEligibilityRequest req) {
        ProgramEnrolment e = findEnrolment(id);
        e.setEligibilityStatus(parseEligibility(req.eligibilityStatus()));
        return EnrolmentResponse.from(enrolmentRepository.save(e));
    }

    private ProgramEnrolment findEnrolment(Long id) {
        return enrolmentRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("ProgramEnrolment", id));
    }

    private EligibilityStatus parseEligibility(String value) {
        try {
            return EligibilityStatus.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid eligibilityStatus: " + value
                    + " (allowed: Eligible, Ineligible, PendingVerification)");
        }
    }
}
