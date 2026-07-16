package com.certifypro.result.client;

import com.certifypro.result.client.dto.PaperDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Resilience4j-guarded wrapper around question-service Feign calls.
 * If question-service is unavailable, paper lookups degrade to null so the
 * caller (marks submit / result compute) can fall back gracefully instead of
 * crashing. Circuit-breaker instance "question-service" is configured in
 * config-repo/result-service.yml.
 */
@Component
public class QuestionServiceGateway {

    private static final Logger log = LoggerFactory.getLogger(QuestionServiceGateway.class);

    private final PaperClient paperClient;

    public QuestionServiceGateway(PaperClient paperClient) {
        this.paperClient = paperClient;
    }

    /** Resolve a paper's totalMarks + window/program (null if question-service is down/unknown). */
    @CircuitBreaker(name = "question-service", fallbackMethod = "getPaperFallback")
    public PaperDto getPaper(Long paperId) {
        return paperClient.getPaper(paperId);
    }

    @SuppressWarnings("unused")
    private PaperDto getPaperFallback(Long paperId, Throwable t) {
        log.warn("question-service unavailable resolving paperId={}: {}. Continuing without paper totals.",
                paperId, t.getMessage());
        return null;
    }
}
