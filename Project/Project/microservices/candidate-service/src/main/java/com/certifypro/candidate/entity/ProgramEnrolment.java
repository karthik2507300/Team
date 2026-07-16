package com.certifypro.candidate.entity;

import com.certifypro.candidate.common.EligibilityStatus;
import com.certifypro.candidate.common.EnrolmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A candidate's enrolment in a certification program.
 * Intra-service relationship to CertificationProgram (@ManyToOne, join col program_id);
 * the candidate is referenced by a plain candidateId (candidate is a sibling aggregate).
 */
@Entity
@Table(name = "program_enrolment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramEnrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrolment_id")
    private Long enrolmentId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private CertificationProgram program;

    @Column(name = "enrolment_date")
    private LocalDate enrolmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_status")
    private EligibilityStatus eligibilityStatus;

    @Column(name = "attempts_used")
    private Integer attemptsUsed;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrolmentStatus status;
}
