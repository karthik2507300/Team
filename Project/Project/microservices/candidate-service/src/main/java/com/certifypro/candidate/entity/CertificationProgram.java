package com.certifypro.candidate.entity;

import com.certifypro.candidate.common.ProgramLevel;
import com.certifypro.candidate.common.ProgramStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "certification_program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long programId;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "body")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private ProgramLevel level;

    @Column(name = "eligibility_criteria", columnDefinition = "text")
    private String eligibilityCriteria;

    @Column(name = "exam_fee")
    private BigDecimal examFee;

    @Column(name = "validity_years")
    private Integer validityYears;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgramStatus status;

    /** Intra-service relationship: the grading bands defined for this program. */
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GradingScale> gradingScale = new ArrayList<>();
}
