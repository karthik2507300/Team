package com.certifypro.result.client;

import com.certifypro.result.client.dto.GradingScaleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client to candidate-service's grading-scale lookup. Supplies the grading
 * bands used to map a percentage to a grade letter / pass-fail outcome during
 * compute. Guarded by the "candidate-service" circuit breaker via CandidateServiceGateway.
 */
@FeignClient(name = "candidate-service", contextId = "programGradingClient", path = "/api/programs")
public interface ProgramGradingClient {

    @GetMapping("/internal/{programId}/grading-scale")
    List<GradingScaleDto> getGradingScale(@PathVariable Long programId);
}
