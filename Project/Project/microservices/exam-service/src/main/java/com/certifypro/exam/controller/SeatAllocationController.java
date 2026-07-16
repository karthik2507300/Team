package com.certifypro.exam.controller;

import com.certifypro.exam.dto.request.CreateSeatAllocationRequest;
import com.certifypro.exam.dto.request.UpdateAllocationStatusRequest;
import com.certifypro.exam.dto.response.ApiResponse;
import com.certifypro.exam.dto.response.SeatAllocationResponse;
import com.certifypro.exam.service.SeatAllocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-allocations")
public class SeatAllocationController {

    private final SeatAllocationService seatAllocationService;

    public SeatAllocationController(SeatAllocationService seatAllocationService) {
        this.seatAllocationService = seatAllocationService;
    }

    /** Candidate registers self, or Admin/CentreAdmin allocates on behalf of a candidate. */
    @PostMapping
    @PreAuthorize("hasAnyRole('Candidate','Admin','CentreAdmin')")
    public ResponseEntity<ApiResponse<SeatAllocationResponse>> allocate(
            @Valid @RequestBody CreateSeatAllocationRequest req) {
        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Seat allocated", seatAllocationService.allocate(req)));
    }

    /** Attendance / room board: allocations filtered by window and/or centre. */
    @GetMapping
    @PreAuthorize("hasAnyRole('CentreAdmin','Admin','ExamController')")
    public ResponseEntity<ApiResponse<List<SeatAllocationResponse>>> listByWindowCentre(
            @RequestParam(required = false) Long windowId,
            @RequestParam(required = false) Long centreId) {
        return ResponseEntity.ok(ApiResponse.ok(seatAllocationService.listByWindowCentre(windowId, centreId)));
    }

    /** Seat + hall ticket info for a candidate. */
    @GetMapping("/{candidateId}")
    public ResponseEntity<ApiResponse<List<SeatAllocationResponse>>> byCandidate(@PathVariable Long candidateId) {
        return ResponseEntity.ok(ApiResponse.ok(seatAllocationService.getByCandidate(candidateId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('Admin','CentreAdmin')")
    public ResponseEntity<ApiResponse<SeatAllocationResponse>> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateAllocationStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Allocation status updated",
                seatAllocationService.updateStatus(id, req)));
    }

    @GetMapping("/{id}/hall-ticket/pdf")
    public ResponseEntity<byte[]> hallTicketPdf(@PathVariable Long id) {
        byte[] pdf = seatAllocationService.generateHallTicket(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=hall-ticket-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
