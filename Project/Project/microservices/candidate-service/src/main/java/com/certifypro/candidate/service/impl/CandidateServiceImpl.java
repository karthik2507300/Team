package com.certifypro.candidate.service.impl;

import com.certifypro.candidate.common.CandidateStatus;
import com.certifypro.candidate.dto.internal.RegisterCandidateRequest;
import com.certifypro.candidate.dto.request.CreateCandidateRequest;
import com.certifypro.candidate.dto.request.UpdateCandidateRequest;
import com.certifypro.candidate.dto.response.CandidateResponse;
import com.certifypro.candidate.entity.Candidate;
import com.certifypro.candidate.exception.BusinessException;
import com.certifypro.candidate.exception.NotFoundException;
import com.certifypro.candidate.repository.CandidateRepository;
import com.certifypro.candidate.security.SecurityUtil;
import com.certifypro.candidate.service.CandidateService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    @Transactional
    public CandidateResponse create(CreateCandidateRequest req) {
        Long userId = SecurityUtil.currentUserId();
        candidateRepository.findByUserId(userId).ifPresent(c -> {
            throw new BusinessException("A candidate profile already exists for this account; use PATCH to edit");
        });

        Candidate c = Candidate.builder()
                .userId(userId)
                .name(req.name())
                .dateOfBirth(req.dateOfBirth())
                .gender(req.gender())
                .email(req.email())
                .phone(req.phone())
                .address(req.address())
                .highestQualification(req.highestQualification())
                .professionalExperience(req.professionalExperience())
                .employerName(req.employerName())
                .status(CandidateStatus.Active)
                .build();
        return CandidateResponse.from(candidateRepository.save(c));
    }

    @Override
    public CandidateResponse getById(Long id) {
        Candidate c = findCandidate(id);
        assertCanAccess(c);
        return CandidateResponse.from(c);
    }

    @Override
    public CandidateResponse getMine() {
        Candidate c = candidateRepository.findByUserId(SecurityUtil.currentUserId())
                .orElseThrow(() -> new NotFoundException("No candidate profile for this account"));
        return CandidateResponse.from(c);
    }

    @Override
    @Transactional
    public CandidateResponse update(Long id, UpdateCandidateRequest req) {
        Candidate c = findCandidate(id);
        assertCanAccess(c);

        if (req.name() != null) c.setName(req.name());
        if (req.dateOfBirth() != null) c.setDateOfBirth(req.dateOfBirth());
        if (req.gender() != null) c.setGender(req.gender());
        if (req.email() != null) c.setEmail(req.email());
        if (req.phone() != null) c.setPhone(req.phone());
        if (req.address() != null) c.setAddress(req.address());
        if (req.highestQualification() != null) c.setHighestQualification(req.highestQualification());
        if (req.professionalExperience() != null) c.setProfessionalExperience(req.professionalExperience());
        if (req.employerName() != null) c.setEmployerName(req.employerName());

        return CandidateResponse.from(candidateRepository.save(c));
    }

    @Override
    @Transactional
    public CandidateResponse register(RegisterCandidateRequest req) {
        if (req.userId() != null) {
            candidateRepository.findByUserId(req.userId()).ifPresent(c -> {
                throw new BusinessException("A candidate profile already exists for this account");
            });
        }
        Candidate c = Candidate.builder()
                .userId(req.userId())
                .name(req.name())
                .email(req.email())
                .phone(req.phone())
                .dateOfBirth(req.dateOfBirth())
                .gender(req.gender())
                .highestQualification(req.highestQualification())
                .professionalExperience(req.professionalExperience())
                .employerName(req.employerName())
                .status(CandidateStatus.Active)
                .build();
        return CandidateResponse.from(candidateRepository.save(c));
    }

    private Candidate findCandidate(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Candidate", id));
    }

    /** Candidates may only touch their own profile; staff/Admin may access any. */
    private void assertCanAccess(Candidate c) {
        if ("Candidate".equals(SecurityUtil.currentRole())
                && !Objects.equals(c.getUserId(), SecurityUtil.currentUserId())) {
            throw new AccessDeniedException("You can only access your own candidate profile");
        }
    }
}
