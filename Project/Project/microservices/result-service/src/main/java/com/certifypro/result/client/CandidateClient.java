package com.certifypro.result.client;

import com.certifypro.result.client.dto.CandidateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to candidate-service's candidate lookup. Used to resolve the
 * candidate's owning userId so the candidate can be notified when a result is
 * published. Guarded by the "candidate-service" circuit breaker via CandidateServiceGateway.
 */
@FeignClient(name = "candidate-service", contextId = "candidateClient", path = "/api/candidates")
public interface CandidateClient {

    @GetMapping("/internal/{candidateId}")
    CandidateDto getCandidate(@PathVariable Long candidateId);
}
