package com.certifypro.result.client;

import com.certifypro.result.client.dto.ExamWindowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to exam-service's exam window lookup (same service, different base
 * path). Used to resolve the programId for a window during result compute.
 * Guarded by the "exam-service" circuit breaker via ExamServiceGateway.
 */
@FeignClient(name = "exam-service", contextId = "examWindowClient", path = "/api/exam-windows")
public interface ExamWindowClient {

    @GetMapping("/internal/{windowId}")
    ExamWindowDto getExamWindow(@PathVariable Long windowId);
}
