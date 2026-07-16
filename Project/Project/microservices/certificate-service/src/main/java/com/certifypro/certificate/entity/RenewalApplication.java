package com.certifypro.certificate.entity;

import com.certifypro.certificate.common.RenewalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A CPD renewal application against a certificate.
 * Intra-service relationship to Certificate (@ManyToOne, join col certificate_id);
 * candidateId / reviewedById reference sibling aggregates and stay plain Long.
 */
@Entity
@Table(name = "renewal_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RenewalApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "renewal_id")
    private Long renewalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "cpd_points_submitted")
    private Integer cpdPointsSubmitted;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "reviewed_by_id")
    private Long reviewedById;

    @Column(name = "new_valid_until")
    private LocalDate newValidUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RenewalStatus status;
}
