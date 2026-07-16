package com.certifypro.certificate.client;

import com.certifypro.certificate.client.dto.CandidateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to candidate-service's candidate lookup. Used to resolve the
 * candidate's owning userId so the candidate can be notified when a certificate
 * is issued.
 */
@FeignClient(name = "candidate-service", contextId = "candidateClient", path = "/api/candidates")
public interface CandidateClient {

    @GetMapping("/internal/{candidateId}")
    CandidateDto getCandidate(@PathVariable Long candidateId);
}
