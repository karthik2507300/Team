package com.certifypro.exam.repository;

import com.certifypro.exam.entity.SeatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Window/centre finders traverse the ExamWindow / TestCentre associations
 * (the raw ids were replaced with @ManyToOne relationships).
 */
public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {

    List<SeatAllocation> findByCandidateId(Long candidateId);

    long countByExamWindow_WindowIdAndTestCentre_CentreId(Long windowId, Long centreId);

    List<SeatAllocation> findByExamWindow_WindowIdAndTestCentre_CentreId(Long windowId, Long centreId);

    List<SeatAllocation> findByExamWindow_WindowId(Long windowId);

    List<SeatAllocation> findByTestCentre_CentreId(Long centreId);
}
