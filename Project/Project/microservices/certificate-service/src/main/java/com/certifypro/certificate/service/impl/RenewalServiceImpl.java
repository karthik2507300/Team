package com.certifypro.certificate.service.impl;

import com.certifypro.certificate.client.CertificateNotificationGateway;
import com.certifypro.certificate.client.dto.CandidateDto;
import com.certifypro.certificate.client.dto.ProgramDto;
import com.certifypro.certificate.common.CertificateStatus;
import com.certifypro.certificate.common.RenewalStatus;
import com.certifypro.certificate.dto.request.CreateRenewalRequest;
import com.certifypro.certificate.dto.request.ReviewRenewalRequest;
import com.certifypro.certificate.dto.response.RenewalResponse;
import com.certifypro.certificate.entity.Certificate;
import com.certifypro.certificate.entity.RenewalApplication;
import com.certifypro.certificate.exception.BusinessException;
import com.certifypro.certificate.exception.NotFoundException;
import com.certifypro.certificate.repository.CertificateRepository;
import com.certifypro.certificate.repository.RenewalApplicationRepository;
import com.certifypro.certificate.security.SecurityUtil;
import com.certifypro.certificate.service.RenewalService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class RenewalServiceImpl implements RenewalService {

    private final RenewalApplicationRepository renewalRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateNotificationGateway gateway;

    public RenewalServiceImpl(RenewalApplicationRepository renewalRepository,
                              CertificateRepository certificateRepository,
                              CertificateNotificationGateway gateway) {
        this.renewalRepository = renewalRepository;
        this.certificateRepository = certificateRepository;
        this.gateway = gateway;
    }

    /** Rule 6: renewal allowed only if certificate is Valid and within 90 days of expiry. */
    @Override
    @Transactional
    public RenewalResponse submit(CreateRenewalRequest req) {
        Certificate cert = certificateRepository.findById(req.certificateId())
                .orElseThrow(() -> NotFoundException.of("Certificate", req.certificateId()));

        // Ownership: the certificate's candidate must belong to the current user.
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            CandidateDto candidate = gateway.getCandidate(cert.getCandidateId());
            if (candidate == null || !Objects.equals(candidate.userId(), SecurityUtil.currentUserId())) {
                throw new AccessDeniedException("You can only renew your own certificates");
            }
        }
        if (cert.getStatus() != CertificateStatus.Valid) {
            throw new BusinessException("Only Valid certificates can be renewed (current: "
                    + cert.getStatus() + ")");
        }
        LocalDate today = LocalDate.now();
        if (cert.getValidUntil() == null || cert.getValidUntil().isAfter(today.plusDays(90))) {
            throw new BusinessException("Renewal is allowed only within 90 days of expiry");
        }

        RenewalApplication r = RenewalApplication.builder()
                .certificate(cert)
                .candidateId(cert.getCandidateId())
                .cpdPointsSubmitted(req.cpdPointsSubmitted())
                .applicationDate(today)
                .status(RenewalStatus.Submitted)
                .build();
        r = renewalRepository.save(r);

        return RenewalResponse.from(r);
    }

    @Override
    public RenewalResponse getById(Long id) {
        return RenewalResponse.from(findRenewal(id));
    }

    @Override
    @Transactional
    public RenewalResponse review(Long id, ReviewRenewalRequest req) {
        RenewalApplication r = findRenewal(id);
        RenewalStatus decision = parseDecision(req.decision());
        r.setReviewedById(SecurityUtil.currentUserId());

        if (decision == RenewalStatus.Approved) {
            Certificate cert = r.getCertificate();
            if (cert == null) {
                throw NotFoundException.of("Certificate", null);
            }
            int extend = resolveExtendYears(req.extendYears(), cert.getProgramId());
            LocalDate base = (cert.getValidUntil() == null || cert.getValidUntil().isBefore(LocalDate.now()))
                    ? LocalDate.now() : cert.getValidUntil();
            LocalDate newValidUntil = base.plusYears(extend);

            cert.setValidUntil(newValidUntil);
            cert.setStatus(CertificateStatus.Valid);
            certificateRepository.save(cert);

            r.setNewValidUntil(newValidUntil);
            r.setStatus(RenewalStatus.Approved);
        } else {
            r.setStatus(RenewalStatus.Rejected);
        }

        RenewalApplication saved = renewalRepository.save(r);
        return RenewalResponse.from(saved);
    }

    private int resolveExtendYears(Integer requested, Long programId) {
        if (requested != null && requested > 0) {
            return requested;
        }
        if (programId != null) {
            ProgramDto program = gateway.getProgram(programId);
            if (program != null && program.validityYears() != null && program.validityYears() > 0) {
                return program.validityYears();
            }
        }
        return 1;
    }

    private RenewalApplication findRenewal(Long id) {
        return renewalRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("RenewalApplication", id));
    }

    private RenewalStatus parseDecision(String value) {
        if ("Approved".equals(value)) return RenewalStatus.Approved;
        if ("Rejected".equals(value)) return RenewalStatus.Rejected;
        throw new IllegalArgumentException("Invalid decision: " + value + " (Approved or Rejected)");
    }
}
