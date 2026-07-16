package com.certifypro.candidate.repository;

import com.certifypro.candidate.common.ProgramStatus;
import com.certifypro.candidate.entity.CertificationProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationProgramRepository extends JpaRepository<CertificationProgram, Long> {

    Page<CertificationProgram> findByStatus(ProgramStatus status, Pageable p);
}
