package com.certifypro.repository;

import com.certifypro.model.CertificationProgram;
import com.certifypro.model.enums.ProgramStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationProgramRepository extends JpaRepository<CertificationProgram, Long> {

    Page<CertificationProgram> findByStatus(ProgramStatus status, Pageable p);
}
