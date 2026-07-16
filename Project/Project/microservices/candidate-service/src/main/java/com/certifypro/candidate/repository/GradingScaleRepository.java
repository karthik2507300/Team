package com.certifypro.candidate.repository;

import com.certifypro.candidate.entity.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    List<GradingScale> findByProgram_ProgramId(Long programId);
}
