package com.certifypro.candidate.repository;

import com.certifypro.candidate.common.EligibilityStatus;
import com.certifypro.candidate.entity.ProgramEnrolment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramEnrolmentRepository extends JpaRepository<ProgramEnrolment, Long> {

    Page<ProgramEnrolment> findByCandidateId(Long candidateId, Pageable p);

    Page<ProgramEnrolment> findByEligibilityStatus(EligibilityStatus s, Pageable p);

    Optional<ProgramEnrolment> findByCandidateIdAndProgram_ProgramId(Long candidateId, Long programId);

    List<ProgramEnrolment> findAllByProgram_ProgramId(Long programId);
}
