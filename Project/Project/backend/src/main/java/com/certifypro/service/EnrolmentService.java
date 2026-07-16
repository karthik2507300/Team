package com.certifypro.service;

import com.certifypro.dto.request.CreateEnrolmentRequest;
import com.certifypro.dto.request.UpdateEligibilityRequest;
import com.certifypro.dto.response.EnrolmentResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Candidate;
import com.certifypro.model.CertificationProgram;
import com.certifypro.model.ProgramEnrolment;
import com.certifypro.model.enums.EligibilityStatus;
import com.certifypro.model.enums.EnrolmentStatus;
import com.certifypro.model.enums.ProgramStatus;
import com.certifypro.repository.CandidateRepository;
import com.certifypro.repository.CertificationProgramRepository;
import com.certifypro.repository.ProgramEnrolmentRepository;
import com.certifypro.security.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class EnrolmentService {

    private final ProgramEnrolmentRepository enrolmentRepository;
    private final CandidateRepository candidateRepository;
    private final CertificationProgramRepository programRepository;

    public EnrolmentService(ProgramEnrolmentRepository enrolmentRepository,
                            CandidateRepository candidateRepository,
                            CertificationProgramRepository programRepository) {
        this.enrolmentRepository = enrolmentRepository;
        this.candidateRepository = candidateRepository;
        this.programRepository = programRepository;
    }

    @Transactional
    public EnrolmentResponse create(CreateEnrolmentRequest req) {
        Candidate candidate = candidateRepository.findByUserId(SecurityUtil.currentUserId())
                .orElseThrow(() -> new BusinessException("Complete your candidate profile before enrolling"));

        CertificationProgram program = programRepository.findById(req.programId())
                .orElseThrow(() -> NotFoundException.of("CertificationProgram", req.programId()));
        if (program.getStatus() != ProgramStatus.Active) {
            throw new BusinessException("This program is not open for enrolment");
        }

        enrolmentRepository.findByCandidateIdAndProgramId(candidate.getCandidateId(), program.getProgramId())
                .ifPresent(e -> {
                    throw new BusinessException("You are already enrolled in this program");
                });

        ProgramEnrolment e = new ProgramEnrolment();
        e.setCandidateId(candidate.getCandidateId());
        e.setProgramId(program.getProgramId());
        e.setEnrolmentDate(LocalDate.now());
        e.setEligibilityStatus(EligibilityStatus.PendingVerification);
        e.setAttemptsUsed(0);
        e.setMaxAttempts(program.getMaxAttempts());
        e.setStatus(EnrolmentStatus.Active);
        return EnrolmentResponse.from(enrolmentRepository.save(e));
    }

    public com.certifypro.dto.response.PageResponse<EnrolmentResponse> list(
            String eligibilityStatus, int page, int limit) {
        var pageable = com.certifypro.util.PageUtil.of(page, limit);
        if (eligibilityStatus != null) {
            return com.certifypro.dto.response.PageResponse.from(
                    enrolmentRepository.findByEligibilityStatus(
                            parseEligibility(eligibilityStatus), pageable).map(EnrolmentResponse::from));
        }
        return com.certifypro.dto.response.PageResponse.from(
                enrolmentRepository.findAll(pageable).map(EnrolmentResponse::from));
    }

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
