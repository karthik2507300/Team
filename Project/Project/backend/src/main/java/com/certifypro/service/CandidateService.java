package com.certifypro.service;

import com.certifypro.dto.request.CreateCandidateRequest;
import com.certifypro.dto.request.UpdateCandidateRequest;
import com.certifypro.dto.response.CandidateResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Candidate;
import com.certifypro.model.enums.CandidateStatus;
import com.certifypro.repository.CandidateRepository;
import com.certifypro.security.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Transactional
    public CandidateResponse create(CreateCandidateRequest req) {
        Long userId = SecurityUtil.currentUserId();
        candidateRepository.findByUserId(userId).ifPresent(c -> {
            throw new BusinessException("A candidate profile already exists for this account; use PATCH to edit");
        });

        Candidate c = new Candidate();
        c.setUserId(userId);
        c.setName(req.name());
        c.setDateOfBirth(req.dateOfBirth());
        c.setGender(req.gender());
        c.setEmail(req.email());
        c.setPhone(req.phone());
        c.setAddress(req.address());
        c.setHighestQualification(req.highestQualification());
        c.setProfessionalExperience(req.professionalExperience());
        c.setEmployerName(req.employerName());
        c.setStatus(CandidateStatus.Active);
        return CandidateResponse.from(candidateRepository.save(c));
    }

    public CandidateResponse getById(Long id) {
        Candidate c = findCandidate(id);
        assertCanAccess(c);
        return CandidateResponse.from(c);
    }

    /** The candidate profile of the currently authenticated user. */
    public CandidateResponse getMine() {
        Candidate c = candidateRepository.findByUserId(SecurityUtil.currentUserId())
                .orElseThrow(() -> new NotFoundException("No candidate profile for this account"));
        return CandidateResponse.from(c);
    }

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
