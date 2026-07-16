package com.certifypro.exam.controller;

import com.certifypro.exam.dto.response.SeatAllocationInternalDto;
import com.certifypro.exam.service.SeatAllocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Service-to-service endpoints (called via Feign, not by the browser).
 * Returns raw DTOs (no ApiResponse envelope). Permitted without a role in
 * SecurityConfig via the /api/**&#47;internal/** matcher.
 */
@RestController
@RequestMapping("/api/seat-allocations/internal")
public class InternalSeatAllocationController {

    private final SeatAllocationService seatAllocationService;

    public InternalSeatAllocationController(SeatAllocationService seatAllocationService) {
        this.seatAllocationService = seatAllocationService;
    }

    @GetMapping("/{allocationId}")
    public SeatAllocationInternalDto getById(@PathVariable Long allocationId) {
        return seatAllocationService.getInternal(allocationId);
    }
}
