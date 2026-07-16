package com.certifypro.certificate.service;

import com.certifypro.certificate.dto.request.IssueCertificateRequest;
import com.certifypro.certificate.dto.request.UpdateCertificateStatusRequest;
import com.certifypro.certificate.dto.response.CertificateResponse;

import java.util.List;

public interface CertificateService {

    /** Certification Officer / Admin manually issues a certificate. */
    CertificateResponse issue(IssueCertificateRequest req);

    /** Service-to-service auto-issue (called by result-service on Pass publish). */
    CertificateResponse issueInternal(Long candidateId, Long programId);

    /** Candidate views own certificates (officer/Admin may view any). */
    List<CertificateResponse> getByCandidate(Long candidateId);

    CertificateResponse updateStatus(Long id, UpdateCertificateStatusRequest req);

    /** Validity tracker: certificates expiring within the next 90 days. */
    List<CertificateResponse> expiringWithin90Days();
}
