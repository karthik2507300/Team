package com.certifypro.certificate.client;

import com.certifypro.certificate.client.dto.CandidateDto;
import com.certifypro.certificate.client.dto.NotifyUserRequest;
import com.certifypro.certificate.client.dto.ProgramDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around the outbound Feign clients.
 * If a downstream service is unavailable the certificate flow still succeeds:
 * lookups degrade to null and notifications are dropped (logged and continue).
 * Circuit-breaker instances "candidate-service" and "notification-service" are
 * configured in config-repo/certificate-service.yml.
 */
@Component
public class CertificateNotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(CertificateNotificationGateway.class);

    private final CandidateClient candidateClient;
    private final ProgramClient programClient;
    private final NotificationClient notificationClient;

    public CertificateNotificationGateway(CandidateClient candidateClient,
                                          ProgramClient programClient,
                                          NotificationClient notificationClient) {
        this.candidateClient = candidateClient;
        this.programClient = programClient;
        this.notificationClient = notificationClient;
    }

    /** Resolve the candidate's owning userId (null if candidate-service is down/unknown). */
    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getCandidateFallback")
    public CandidateDto getCandidate(Long candidateId) {
        return candidateClient.getCandidate(candidateId);
    }

    @SuppressWarnings("unused")
    private CandidateDto getCandidateFallback(Long candidateId, Throwable t) {
        log.warn("candidate-service unavailable resolving candidateId={}: {}. Continuing without user lookup.",
                candidateId, t.getMessage());
        return null;
    }

    /** Resolve a program's validityYears (null if candidate-service is down/unknown). */
    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getProgramFallback")
    public ProgramDto getProgram(Long programId) {
        return programClient.getProgram(programId);
    }

    @SuppressWarnings("unused")
    private ProgramDto getProgramFallback(Long programId, Throwable t) {
        log.warn("candidate-service unavailable resolving programId={}: {}. Defaulting program validity.",
                programId, t.getMessage());
        return null;
    }

    /** Notify a single user; on failure log and continue (notification is best-effort). */
    @CircuitBreaker(name = "notification-service", fallbackMethod = "notifyUserFallback")
    public void notifyUser(Long userId, String category, String message) {
        notificationClient.notifyUser(new NotifyUserRequest(userId, message, category));
    }

    @SuppressWarnings("unused")
    private void notifyUserFallback(Long userId, String category, String message, Throwable t) {
        log.warn("notification-service unavailable notifying userId={}: {}. Notification dropped.",
                userId, t.getMessage());
    }
}
