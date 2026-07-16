package com.certifypro.exam.service;

import com.certifypro.exam.dto.request.CreateSeatAllocationRequest;
import com.certifypro.exam.dto.request.UpdateAllocationStatusRequest;
import com.certifypro.exam.dto.response.SeatAllocationInternalDto;
import com.certifypro.exam.dto.response.SeatAllocationResponse;

import java.util.List;

public interface SeatAllocationService {

    SeatAllocationResponse allocate(CreateSeatAllocationRequest req);

    List<SeatAllocationResponse> listByWindowCentre(Long windowId, Long centreId);

    List<SeatAllocationResponse> getByCandidate(Long candidateId);

    SeatAllocationResponse updateStatus(Long id, UpdateAllocationStatusRequest req);

    byte[] generateHallTicket(Long allocationId);

    /** Raw projection for service-to-service consumers. */
    SeatAllocationInternalDto getInternal(Long allocationId);
}
