package com.certifypro.result.client;

import com.certifypro.result.client.dto.SeatAllocationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to exam-service's seat allocation lookup. Used to resolve the
 * candidate a script allocation belongs to. Guarded by the "exam-service"
 * circuit breaker via ExamServiceGateway.
 */
@FeignClient(name = "exam-service", contextId = "seatAllocationClient", path = "/api/seat-allocations")
public interface SeatAllocationClient {

    @GetMapping("/internal/{allocationId}")
    SeatAllocationDto getSeatAllocation(@PathVariable Long allocationId);
}
