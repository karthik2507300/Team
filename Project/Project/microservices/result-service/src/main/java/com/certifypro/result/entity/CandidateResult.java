package com.certifypro.result.entity;

import com.certifypro.result.common.ResultOutcome;
import com.certifypro.result.common.ResultStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * The computed result for a candidate in a window/program. candidateId, windowId
 * and programId reference rows owned by other services so they stay plain Long ids.
 */
@Entity
@Table(name = "candidate_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "window_id")
    private Long windowId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @Column(name = "marks_obtained")
    private Integer marksObtained;

    @Column(name = "percentage")
    private Float percentage;

    @Column(name = "grade")
    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome")
    private ResultOutcome outcome;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ResultStatus status;
}
