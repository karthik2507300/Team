package com.certifypro.model;

import com.certifypro.model.enums.ProgramLevel;
import com.certifypro.model.enums.ProgramStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "certification_program")
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

    public CertificationProgram() {
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ProgramLevel getLevel() {
        return level;
    }

    public void setLevel(ProgramLevel level) {
        this.level = level;
    }

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public BigDecimal getExamFee() {
        return examFee;
    }

    public void setExamFee(BigDecimal examFee) {
        this.examFee = examFee;
    }

    public Integer getValidityYears() {
        return validityYears;
    }

    public void setValidityYears(Integer validityYears) {
        this.validityYears = validityYears;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public ProgramStatus getStatus() {
        return status;
    }

    public void setStatus(ProgramStatus status) {
        this.status = status;
    }
}
