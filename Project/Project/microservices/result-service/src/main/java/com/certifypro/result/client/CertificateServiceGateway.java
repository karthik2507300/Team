package com.certifypro.result.client;

import com.certifypro.result.client.dto.CertificateDto;
import com.certifypro.result.client.dto.IssueCertificateRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around certificate-service. Auto-issues a
 * certificate on Pass publish; if certificate-service is unavailable the publish
 * still succeeds (the failure is logged and the certificate can be issued later).
 * Circuit-breaker instance "certificate-service" is configured in
 * config-repo/result-service.yml.
 */
@Component
public class CertificateServiceGateway {

    private static final Logger log = LoggerFactory.getLogger(CertificateServiceGateway.class);

    private final CertificateClient certificateClient;

    public CertificateServiceGateway(CertificateClient certificateClient) {
        this.certificateClient = certificateClient;
    }

    /** Auto-issue a certificate; on failure log and continue (issuance is best-effort). */
    @CircuitBreaker(name = "certificate-service", fallbackMethod = "issueFallback")
    public CertificateDto issue(Long candidateId, Long programId) {
        return certificateClient.issue(new IssueCertificateRequest(candidateId, programId));
    }

    @SuppressWarnings("unused")
    private CertificateDto issueFallback(Long candidateId, Long programId, Throwable t) {
        log.warn("certificate-service unavailable issuing certificate for candidateId={} programId={}: {}. "
                + "Certificate NOT auto-issued; publish continues.", candidateId, programId, t.getMessage());
        return null;
    }
}
