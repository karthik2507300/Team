package com.certifypro.repository;

import com.certifypro.model.InvigilatorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvigilatorAssignmentRepository extends JpaRepository<InvigilatorAssignment, Long> {

    List<InvigilatorAssignment> findByWindowIdAndCentreId(Long windowId, Long centreId);

    List<InvigilatorAssignment> findByWindowId(Long windowId);
}
