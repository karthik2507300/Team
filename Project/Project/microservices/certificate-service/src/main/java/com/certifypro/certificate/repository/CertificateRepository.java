package com.certifypro.certificate.repository;

import com.certifypro.certificate.common.CertificateStatus;
import com.certifypro.certificate.entity.Certificate;
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
