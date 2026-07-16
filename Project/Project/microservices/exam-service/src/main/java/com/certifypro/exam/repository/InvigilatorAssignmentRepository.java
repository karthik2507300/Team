package com.certifypro.exam.repository;

import com.certifypro.exam.entity.InvigilatorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Window/centre finders traverse the ExamWindow / TestCentre associations
 * (the raw ids were replaced with @ManyToOne relationships).
 */
public interface InvigilatorAssignmentRepository extends JpaRepository<InvigilatorAssignment, Long> {

    List<InvigilatorAssignment> findByExamWindow_WindowIdAndTestCentre_CentreId(Long windowId, Long centreId);

    List<InvigilatorAssignment> findByExamWindow_WindowId(Long windowId);
}
