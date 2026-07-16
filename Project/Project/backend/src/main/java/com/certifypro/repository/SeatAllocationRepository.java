package com.certifypro.repository;

import com.certifypro.model.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {

    List<SeatAllocation> findByCandidateId(Long candidateId);

    long countByWindowIdAndCentreId(Long windowId, Long centreId);

    List<SeatAllocation> findByWindowIdAndCentreId(Long windowId, Long centreId);

    List<SeatAllocation> findByWindowId(Long windowId);

    List<SeatAllocation> findByCentreId(Long centreId);
}
