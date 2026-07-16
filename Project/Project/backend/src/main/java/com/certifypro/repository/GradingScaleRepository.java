package com.certifypro.repository;

import com.certifypro.model.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {

    List<GradingScale> findByProgramId(Long programId);
}
