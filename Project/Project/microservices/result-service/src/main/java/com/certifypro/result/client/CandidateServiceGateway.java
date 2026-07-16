package com.certifypro.result.client;

import com.certifypro.result.client.dto.CandidateDto;
import com.certifypro.result.client.dto.GradingScaleDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resilience4j-guarded wrapper around candidate-service Feign calls (grading scale
 * + candidate lookup). If candidate-service is unavailable, the grading scale
 * degrades to an empty list (caller applies a default scale) and candidate lookup
 * degrades to null (notification is dropped). Circuit-breaker instance
 * "candidate-service" is configured in config-repo/result-service.yml.
 */
@Component
public class CandidateServiceGateway {

    private static final Logger log = LoggerFactory.getLogger(CandidateServiceGateway.class);

    private final ProgramGradingClient programGradingClient;
    private final CandidateClient candidateClient;

    public CandidateServiceGateway(ProgramGradingClient programGradingClient,
                                   CandidateClient candidateClient) {
        this.programGradingClient = programGradingClient;
        this.candidateClient = candidateClient;
    }

    /** Fetch the program's grading bands (empty list if candidate-service is down/unknown). */
    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getGradingScaleFallback")
    public List<GradingScaleDto> getGradingScale(Long programId) {
        List<GradingScaleDto> scale = programGradingClient.getGradingScale(programId);
        return scale == null ? List.of() : scale;
    }

    @SuppressWarnings("unused")
    private List<GradingScaleDto> getGradingScaleFallback(Long programId, Throwable t) {
        log.warn("candidate-service unavailable resolving grading scale for programId={}: {}. "
                + "Falling back to default grading scale.", programId, t.getMessage());
        return List.of();
    }

    /** Resolve the candidate's owning userId (null if candidate-service is down/unknown). */
    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getCandidateFallback")
    public CandidateDto getCandidate(Long candidateId) {
        return candidateClient.getCandidate(candidateId);
    }

    @SuppressWarnings("unused")
    private CandidateDto getCandidateFallback(Long candidateId, Throwable t) {
        log.warn("candidate-service unavailable resolving candidateId={}: {}. Notification skipped.",
                candidateId, t.getMessage());
        return null;
    }
}
