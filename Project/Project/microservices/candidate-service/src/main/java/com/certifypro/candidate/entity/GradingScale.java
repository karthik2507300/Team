package com.certifypro.candidate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A grade band belonging to a certification program. Co-located with
 * CertificationProgram (was RESULT module in the monolith).
 */
@Entity
@Table(name = "grading_scale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long gradeId;

    /** Intra-service relationship to the owning program (join col program_id). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private CertificationProgram program;

    @Column(name = "grade_letter")
    private String gradeLetter;

    @Column(name = "min_percentage")
    private Integer minPercentage;

    @Column(name = "max_percentage")
    private Integer maxPercentage;

    @Column(name = "is_passing")
    private Boolean isPassing;
}
