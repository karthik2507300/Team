package com.certifypro.result.client;

import com.certifypro.result.client.dto.ExamWindowDto;
import com.certifypro.result.client.dto.SeatAllocationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around exam-service Feign calls.
 * If exam-service is unavailable, seat/window lookups degrade to null so compute
 * can fall back gracefully. Circuit-breaker instance "exam-service" is configured
 * in config-repo/result-service.yml.
 */
@Component
public class ExamServiceGateway {

    private static final Logger log = LoggerFactory.getLogger(ExamServiceGateway.class);

    private final SeatAllocationClient seatAllocationClient;
    private final ExamWindowClient examWindowClient;

    public ExamServiceGateway(SeatAllocationClient seatAllocationClient,
                              ExamWindowClient examWindowClient) {
        this.seatAllocationClient = seatAllocationClient;
        this.examWindowClient = examWindowClient;
    }

    /** Resolve which candidate a seat allocation belongs to (null if exam-service is down/unknown). */
    @CircuitBreaker(name = "exam-service", fallbackMethod = "getSeatAllocationFallback")
    public SeatAllocationDto getSeatAllocation(Long allocationId) {
        return seatAllocationClient.getSeatAllocation(allocationId);
    }

    @SuppressWarnings("unused")
    private SeatAllocationDto getSeatAllocationFallback(Long allocationId, Throwable t) {
        log.warn("exam-service unavailable resolving allocationId={}: {}. Skipping this script in compute.",
                allocationId, t.getMessage());
        return null;
    }

    /** Resolve the programId for a window (null if exam-service is down/unknown). */
    @CircuitBreaker(name = "exam-service", fallbackMethod = "getExamWindowFallback")
    public ExamWindowDto getExamWindow(Long windowId) {
        return examWindowClient.getExamWindow(windowId);
    }

    @SuppressWarnings("unused")
    private ExamWindowDto getExamWindowFallback(Long windowId, Throwable t) {
        log.warn("exam-service unavailable resolving windowId={}: {}. Deriving programId from local data.",
                windowId, t.getMessage());
        return null;
    }
}
