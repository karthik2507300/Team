package com.certifypro.service;

import com.certifypro.dto.request.CreateRenewalRequest;
import com.certifypro.dto.request.ReviewRenewalRequest;
import com.certifypro.dto.response.RenewalResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Candidate;
import com.certifypro.model.Certificate;
import com.certifypro.model.CertificationProgram;
import com.certifypro.model.RenewalApplication;
import com.certifypro.model.enums.CertificateStatus;
import com.certifypro.model.enums.RenewalStatus;
import com.certifypro.repository.CandidateRepository;
import com.certifypro.repository.CertificateRepository;
import com.certifypro.repository.CertificationProgramRepository;
import com.certifypro.repository.RenewalApplicationRepository;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.AuditLogUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class RenewalService {

    private static final String MODULE = "RenewalApplication";

    private final RenewalApplicationRepository renewalRepository;
    private final CertificateRepository certificateRepository;
    private final CertificationProgramRepository programRepository;
    private final CandidateRepository candidateRepository;
    private final AuditLogUtil auditLog;

    public RenewalService(RenewalApplicationRepository renewalRepository,
                          CertificateRepository certificateRepository,
                          CertificationProgramRepository programRepository,
                          CandidateRepository candidateRepository,
                          AuditLogUtil auditLog) {
        this.renewalRepository = renewalRepository;
        this.certificateRepository = certificateRepository;
        this.programRepository = programRepository;
        this.candidateRepository = candidateRepository;
        this.auditLog = auditLog;
    }

    /** Rule 6: renewal allowed only if certificate is Valid and within 90 days of expiry. */
    @Transactional
    public RenewalResponse submit(CreateRenewalRequest req) {
        Candidate candidate = candidateRepository.findByUserId(SecurityUtil.currentUserId())
                .orElseThrow(() -> new AccessDeniedException("No candidate profile for this account"));
        Certificate cert = certificateRepository.findById(req.certificateId())
                .orElseThrow(() -> NotFoundException.of("Certificate", req.certificateId()));
        if (!Objects.equals(cert.getCandidateId(), candidate.getCandidateId())) {
            throw new AccessDeniedException("You can only renew your own certificates");
        }
        if (cert.getStatus() != CertificateStatus.Valid) {
            throw new BusinessException("Only Valid certificates can be renewed (current: "
                    + cert.getStatus() + ")");
        }
        LocalDate today = LocalDate.now();
        if (cert.getValidUntil() == null || cert.getValidUntil().isAfter(today.plusDays(90))) {
            throw new BusinessException("Renewal is allowed only within 90 days of expiry");
        }

        RenewalApplication r = new RenewalApplication();
        r.setCertificateId(req.certificateId());
        r.setCandidateId(candidate.getCandidateId());
        r.setCpdPointsSubmitted(req.cpdPointsSubmitted());
        r.setApplicationDate(today);
        r.setStatus(RenewalStatus.Submitted);
        r = renewalRepository.save(r);

        auditLog.log("CREATE", MODULE, r.getRenewalId());
        return RenewalResponse.from(r);
    }

    public RenewalResponse getById(Long id) {
        return RenewalResponse.from(findRenewal(id));
    }

    @Transactional
    public RenewalResponse review(Long id, ReviewRenewalRequest req) {
        RenewalApplication r = findRenewal(id);
        RenewalStatus decision = parseDecision(req.decision());
        r.setReviewedById(SecurityUtil.currentUserId());

        if (decision == RenewalStatus.Approved) {
            Long certificateId = r.getCertificateId();
            Certificate cert = certificateRepository.findById(certificateId)
                    .orElseThrow(() -> NotFoundException.of("Certificate", certificateId));
            int extend = resolveExtendYears(req.extendYears(), cert.getProgramId());
            LocalDate base = (cert.getValidUntil() == null || cert.getValidUntil().isBefore(LocalDate.now()))
                    ? LocalDate.now() : cert.getValidUntil();
            LocalDate newValidUntil = base.plusYears(extend);

            cert.setValidUntil(newValidUntil);
            cert.setStatus(CertificateStatus.Valid);
            certificateRepository.save(cert);
            auditLog.log("RENEW_EXTEND", "Certificate", cert.getCertificateId());

            r.setNewValidUntil(newValidUntil);
            r.setStatus(RenewalStatus.Approved);
        } else {
            r.setStatus(RenewalStatus.Rejected);
        }

        RenewalApplication saved = renewalRepository.save(r);
        auditLog.log("REVIEW", MODULE, saved.getRenewalId());
        return RenewalResponse.from(saved);
    }

    private int resolveExtendYears(Integer requested, Long programId) {
        if (requested != null && requested > 0) {
            return requested;
        }
        CertificationProgram program = programRepository.findById(programId).orElse(null);
        return (program != null && program.getValidityYears() != null) ? program.getValidityYears() : 1;
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
