package com.certifypro.exam.client;

import com.certifypro.exam.client.dto.ProgramDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to candidate-service's program endpoints. Used to fetch the
 * program name for the hall ticket.
 */
@FeignClient(name = "candidate-service", contextId = "programClient", path = "/api/programs")
public interface ProgramClient {

    @GetMapping("/internal/{programId}")
    ProgramDto getProgram(@PathVariable Long programId);
}
