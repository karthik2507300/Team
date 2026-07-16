package com.certifypro.repository;

import com.certifypro.model.ProgramEnrolment;
import com.certifypro.model.enums.EligibilityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramEnrolmentRepository extends JpaRepository<ProgramEnrolment, Long> {

    Page<ProgramEnrolment> findByCandidateId(Long candidateId, Pageable p);

    Page<ProgramEnrolment> findByEligibilityStatus(EligibilityStatus s, Pageable p);

    Optional<ProgramEnrolment> findByCandidateIdAndProgramId(Long candidateId, Long programId);

    List<ProgramEnrolment> findAllByProgramId(Long programId);
}
