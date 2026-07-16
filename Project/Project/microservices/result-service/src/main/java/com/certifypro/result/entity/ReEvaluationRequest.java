package com.certifypro.result.entity;

import com.certifypro.result.common.ReEvalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A candidate's request to re-evaluate a published result.
 * Intra-service relationship: @ManyToOne CandidateResult (join column result_id).
 * candidateId is a candidate-service id, kept as plain Long.
 */
@Entity
@Table(name = "re_evaluation_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReEvaluationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private CandidateResult result;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "reason", columnDefinition = "text")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReEvalStatus status;
}
