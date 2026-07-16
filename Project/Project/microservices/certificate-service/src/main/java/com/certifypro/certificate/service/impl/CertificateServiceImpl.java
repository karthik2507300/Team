package com.certifypro.certificate.service.impl;

import com.certifypro.certificate.client.CertificateNotificationGateway;
import com.certifypro.certificate.client.dto.CandidateDto;
import com.certifypro.certificate.client.dto.ProgramDto;
import com.certifypro.certificate.common.CertificateStatus;
import com.certifypro.certificate.dto.request.IssueCertificateRequest;
import com.certifypro.certificate.dto.request.UpdateCertificateStatusRequest;
import com.certifypro.certificate.dto.response.CertificateResponse;
import com.certifypro.certificate.entity.Certificate;
import com.certifypro.certificate.exception.NotFoundException;
import com.certifypro.certificate.repository.CertificateRepository;
import com.certifypro.certificate.security.SecurityUtil;
import com.certifypro.certificate.service.CertificateService;
import com.certifypro.certificate.util.CertificateNumberGenerator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateServiceImpl implements CertificateService {

    /** Fallback validity when the program's validityYears is unknown/unavailable. */
    private static final int DEFAULT_VALIDITY_YEARS = 3;

    private final CertificateRepository certificateRepository;
    private final CertificateNumberGenerator numberGenerator;
    private final CertificateNotificationGateway gateway;

    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  CertificateNumberGenerator numberGenerator,
                                  CertificateNotificationGateway gateway) {
        this.certificateRepository = certificateRepository;
        this.numberGenerator = numberGenerator;
        this.gateway = gateway;
    }

    @Override
    @Transactional
    public CertificateResponse issue(IssueCertificateRequest req) {
        return doIssue(req.candidateId(), req.programId(), SecurityUtil.currentUserId());
    }

    @Override
    @Transactional
    public CertificateResponse issueInternal(Long candidateId, Long programId) {
        return doIssue(candidateId, programId, null);
    }

    /**
     * Simplified vs. the monolith: cross-service candidate/program existence checks
     * are dropped (spec rule 2). Program validity is resolved best-effort via Feign;
     * when unavailable it defaults to {@link #DEFAULT_VALIDITY_YEARS} years.
     */
    private CertificateResponse doIssue(Long candidateId, Long programId, Long issuedById) {
        int validityYears = resolveValidityYears(programId);

        LocalDate issued = LocalDate.now();
        Certificate cert = Certificate.builder()
                .candidateId(candidateId)
                .programId(programId)
                .certificateNumber(numberGenerator.generate())
                .issuedDate(issued)
                .validUntil(issued.plusYears(validityYears))
                .issuedById(issuedById)
                .status(CertificateStatus.Valid)
                .build();
        cert = certificateRepository.save(cert);

        notifyCandidate(candidateId, cert.getCertificateNumber());
        return CertificateResponse.from(cert);
    }

    private int resolveValidityYears(Long programId) {
        if (programId == null) {
            return DEFAULT_VALIDITY_YEARS;
        }
        ProgramDto program = gateway.getProgram(programId);
        return (program != null && program.validityYears() != null && program.validityYears() > 0)
                ? program.validityYears() : DEFAULT_VALIDITY_YEARS;
    }

    private void notifyCandidate(Long candidateId, String certificateNumber) {
        CandidateDto candidate = gateway.getCandidate(candidateId);
        if (candidate != null && candidate.userId() != null) {
            gateway.notifyUser(candidate.userId(), "Certificate",
                    "Your certificate " + certificateNumber + " has been issued.");
        }
    }

    @Override
    public List<CertificateResponse> getByCandidate(Long candidateId) {
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            CandidateDto own = gateway.getCandidate(candidateId);
            if (own == null || !Objects.equals(own.userId(), SecurityUtil.currentUserId())) {
                throw new AccessDeniedException("You can only view your own certificates");
            }
        }
        return certificateRepository.findByCandidateId(candidateId).stream()
                .map(CertificateResponse::from).toList();
    }

    @Override
    @Transactional
    public CertificateResponse updateStatus(Long id, UpdateCertificateStatusRequest req) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Certificate", id));
        cert.setStatus(parseStatus(req.status()));
        cert = certificateRepository.save(cert);
        return CertificateResponse.from(cert);
    }

    @Override
    public List<CertificateResponse> expiringWithin90Days() {
        LocalDate today = LocalDate.now();
        return certificateRepository
                .findByStatusAndValidUntilBetween(CertificateStatus.Valid, today, today.plusDays(90))
                .stream().map(CertificateResponse::from).toList();
    }

    private CertificateStatus parseStatus(String value) {
        try {
            return CertificateStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + value
                    + " (Valid, Expired, Revoked, Suspended)");
        }
    }
}
