package com.certifypro.service;

import com.certifypro.dto.request.IssueCertificateRequest;
import com.certifypro.dto.request.UpdateCertificateStatusRequest;
import com.certifypro.dto.response.CertificateResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Candidate;
import com.certifypro.model.Certificate;
import com.certifypro.model.CertificationProgram;
import com.certifypro.model.enums.CertificateStatus;
import com.certifypro.repository.CandidateRepository;
import com.certifypro.repository.CertificateRepository;
import com.certifypro.repository.CertificationProgramRepository;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.AuditLogUtil;
import com.certifypro.util.CertificateNumberGenerator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateService {

    private static final String MODULE = "Certificate";

    private final CertificateRepository certificateRepository;
    private final CertificationProgramRepository programRepository;
    private final CandidateRepository candidateRepository;
    private final CertificateNumberGenerator numberGenerator;
    private final NotificationService notificationService;
    private final AuditLogUtil auditLog;

    public CertificateService(CertificateRepository certificateRepository,
                              CertificationProgramRepository programRepository,
                              CandidateRepository candidateRepository,
                              CertificateNumberGenerator numberGenerator,
                              NotificationService notificationService,
                              AuditLogUtil auditLog) {
        this.certificateRepository = certificateRepository;
        this.programRepository = programRepository;
        this.candidateRepository = candidateRepository;
        this.numberGenerator = numberGenerator;
        this.notificationService = notificationService;
        this.auditLog = auditLog;
    }

    @Transactional
    public CertificateResponse issue(IssueCertificateRequest req) {
        if (!candidateRepository.existsById(req.candidateId())) {
            throw NotFoundException.of("Candidate", req.candidateId());
        }
        CertificationProgram program = programRepository.findById(req.programId())
                .orElseThrow(() -> NotFoundException.of("CertificationProgram", req.programId()));

        LocalDate issued = LocalDate.now();
        Certificate cert = new Certificate();
        cert.setCandidateId(req.candidateId());
        cert.setProgramId(req.programId());
        cert.setCertificateNumber(numberGenerator.generate());
        cert.setIssuedDate(issued);
        cert.setValidUntil(program.getValidityYears() == null ? null
                : issued.plusYears(program.getValidityYears()));
        cert.setIssuedById(SecurityUtil.currentUserId());
        cert.setStatus(CertificateStatus.Valid);
        cert = certificateRepository.save(cert);

        auditLog.log("ISSUE", MODULE, cert.getCertificateId());

        final Certificate issuedCert = cert;
        candidateRepository.findById(req.candidateId())
                .map(Candidate::getUserId)
                .ifPresent(uid -> notificationService.notifyUser(uid, "Certificate",
                        "Your certificate " + issuedCert.getCertificateNumber() + " has been issued."));

        return CertificateResponse.from(cert);
    }

    public List<CertificateResponse> getByCandidate(Long candidateId) {
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            Candidate own = candidateRepository.findByUserId(SecurityUtil.currentUserId()).orElse(null);
            if (own == null || !Objects.equals(own.getCandidateId(), candidateId)) {
                throw new AccessDeniedException("You can only view your own certificates");
            }
        }
        return certificateRepository.findByCandidateId(candidateId).stream()
                .map(CertificateResponse::from).toList();
    }

    @Transactional
    public CertificateResponse updateStatus(Long id, UpdateCertificateStatusRequest req) {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Certificate", id));
        cert.setStatus(parseStatus(req.status()));
        cert = certificateRepository.save(cert);
        auditLog.log("STATUS_CHANGE", MODULE, cert.getCertificateId());
        return CertificateResponse.from(cert);
    }

    /** Validity tracker: certificates expiring within the next 90 days. */
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
