package com.certifypro.certificate.entity;

import com.certifypro.certificate.common.CertificateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A certificate issued to a candidate for a certification program.
 * candidateId / programId / issuedById reference sibling aggregates in other
 * services and are kept as plain Long id columns (no JPA relationship, no FK).
 */
@Entity
@Table(name = "certificate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "issued_by_id")
    private Long issuedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CertificateStatus status;
}
