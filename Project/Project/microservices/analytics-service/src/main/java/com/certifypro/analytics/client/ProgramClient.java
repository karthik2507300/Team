package com.certifypro.analytics.client;

import com.certifypro.analytics.client.dto.ProgramDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to candidate-service. Analytics uses it to resolve the human
 * readable program details when generating a Program-scope report.
 * This is the one cross-service endpoint the spec exposes that analytics can
 * meaningfully consume today; the numeric aggregates require additional
 * internal endpoints (Phase 2) that the spec does not yet define.
 */
@FeignClient(name = "candidate-service", path = "/api/programs")
public interface ProgramClient {

    @GetMapping("/internal/{programId}")
    ProgramDto getProgram(@PathVariable Long programId);
}
