package com.certifypro.exam.client;

import com.certifypro.exam.client.dto.CandidateDto;
import com.certifypro.exam.client.dto.ProgramDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around {@link CandidateClient} and {@link ProgramClient}.
 * If candidate-service is unavailable, the hall ticket is still generated with
 * whatever exam-window/centre data is local; the candidate/program fields degrade
 * to null (rendered as "-" by the PDF generator).
 */
@Component
public class CandidateProfileGateway {

    private static final Logger log = LoggerFactory.getLogger(CandidateProfileGateway.class);

    private final CandidateClient candidateClient;
    private final ProgramClient programClient;

    public CandidateProfileGateway(CandidateClient candidateClient, ProgramClient programClient) {
        this.candidateClient = candidateClient;
        this.programClient = programClient;
    }

    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getCandidateFallback")
    public CandidateDto getCandidate(Long candidateId) {
        return candidateClient.getCandidate(candidateId);
    }

    @SuppressWarnings("unused")
    private CandidateDto getCandidateFallback(Long candidateId, Throwable t) {
        log.warn("candidate-service unavailable fetching candidateId={}: {}. Returning null.",
                candidateId, t.getMessage());
        return null;
    }

    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getProgramFallback")
    public ProgramDto getProgram(Long programId) {
        return programClient.getProgram(programId);
    }

    @SuppressWarnings("unused")
    private ProgramDto getProgramFallback(Long programId, Throwable t) {
        log.warn("candidate-service unavailable fetching programId={}: {}. Returning null.",
                programId, t.getMessage());
        return null;
    }
}
