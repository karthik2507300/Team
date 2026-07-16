package com.certifypro.certificate.client;

import com.certifypro.certificate.client.dto.ProgramDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to candidate-service's program lookup (same service, different
 * base path). Used to resolve a program's validityYears when issuing / renewing
 * a certificate. Guarded by the "candidate-service" circuit breaker via the gateway.
 */
@FeignClient(name = "candidate-service", contextId = "programClient", path = "/api/programs")
public interface ProgramClient {

    @GetMapping("/internal/{programId}")
    ProgramDto getProgram(@PathVariable Long programId);
}
