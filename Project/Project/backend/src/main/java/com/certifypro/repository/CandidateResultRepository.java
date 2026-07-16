package com.certifypro.repository;

import com.certifypro.model.CandidateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateResultRepository extends JpaRepository<CandidateResult, Long> {

    Page<CandidateResult> findByCandidateId(Long candidateId, Pageable p);

    Page<CandidateResult> findByWindowId(Long windowId, Pageable p);

    Page<CandidateResult> findByCandidateIdAndWindowId(Long candidateId, Long windowId, Pageable p);

    List<CandidateResult> findAllByWindowId(Long windowId);

    java.util.Optional<CandidateResult> findByCandidateIdAndWindowIdAndProgramId(
            Long candidateId, Long windowId, Long programId);

    List<CandidateResult> findAllByProgramId(Long programId);
}
