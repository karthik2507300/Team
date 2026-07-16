package com.certifypro.analytics.client;

import com.certifypro.analytics.client.dto.ProgramDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around {@link ProgramClient}. If candidate-service
 * is unavailable the report is still produced — the program label is simply
 * omitted (null) rather than failing the whole generate() call.
 */
@Component
public class ProgramGateway {

    private static final Logger log = LoggerFactory.getLogger(ProgramGateway.class);

    private final ProgramClient programClient;

    public ProgramGateway(ProgramClient programClient) {
        this.programClient = programClient;
    }

    @CircuitBreaker(name = "candidate-service", fallbackMethod = "getProgramFallback")
    public ProgramDto getProgram(Long programId) {
        return programClient.getProgram(programId);
    }

    @SuppressWarnings("unused")
    private ProgramDto getProgramFallback(Long programId, Throwable t) {
        log.warn("candidate-service unavailable resolving programId={}: {}. "
                + "Report will omit the program label.", programId, t.getMessage());
        return null;
    }
}
