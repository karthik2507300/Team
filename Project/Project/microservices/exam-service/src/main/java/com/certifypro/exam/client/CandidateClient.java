package com.certifypro.exam.client;

import com.certifypro.exam.client.dto.CandidateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to candidate-service. Used to fetch the candidate (name for the
 * hall ticket, and userId for seat-allocation notifications).
 */
@FeignClient(name = "candidate-service", contextId = "candidateClient", path = "/api/candidates")
public interface CandidateClient {

    @GetMapping("/internal/{candidateId}")
    CandidateDto getCandidate(@PathVariable Long candidateId);
}
