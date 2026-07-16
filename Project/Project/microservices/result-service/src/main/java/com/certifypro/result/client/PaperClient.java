package com.certifypro.result.client;

import com.certifypro.result.client.dto.PaperDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to question-service's paper lookup. Supplies the paper's totalMarks
 * (for dual-marking moderation + compute) and the window/program it belongs to.
 * Guarded by the "question-service" circuit breaker via QuestionServiceGateway.
 */
@FeignClient(name = "question-service", contextId = "paperClient", path = "/api/question-papers")
public interface PaperClient {

    @GetMapping("/internal/{paperId}")
    PaperDto getPaper(@PathVariable Long paperId);
}
