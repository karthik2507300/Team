package com.certifypro.repository;

import com.certifypro.model.Certificate;
import com.certifypro.model.enums.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByCandidateId(Long candidateId);

    Optional<Certificate> findByCertificateNumber(String number);

    List<Certificate> findByStatusAndValidUntilBetween(CertificateStatus status, LocalDate from, LocalDate to);

    List<Certificate> findAllByProgramId(Long programId);
}
