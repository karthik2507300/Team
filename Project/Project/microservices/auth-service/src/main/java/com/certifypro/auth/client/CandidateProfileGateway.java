package com.certifypro.auth.client;

import com.certifypro.auth.client.dto.CreateCandidateProfileRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around {@link CandidateClient}.
 * If candidate-service is unavailable, registration still succeeds (the User is
 * created) and the candidate can complete their profile later via /api/candidates.
 */
@Component
public class CandidateProfileGateway {

    private static final Logger log = LoggerFactory.getLogger(CandidateProfileGateway.class);

    private final CandidateClient candidateClient;

    public CandidateProfileGateway(CandidateClient candidateClient) {
        this.candidateClient = candidateClient;
    }

    @CircuitBreaker(name = "candidate-service", fallbackMethod = "createProfileFallback")
    public void createProfile(CreateCandidateProfileRequest request) {
        candidateClient.createProfile(request);
    }

    @SuppressWarnings("unused")
    private void createProfileFallback(CreateCandidateProfileRequest request, Throwable t) {
        log.warn("candidate-service unavailable during registration for userId={}: {}. "
                + "Profile creation deferred.", request.userId(), t.getMessage());
    }
}
