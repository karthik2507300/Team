package com.certifypro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grading_scale")
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grade_id")
    private Long gradeId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "grade_letter")
    private String gradeLetter;

    @Column(name = "min_percentage")
    private Integer minPercentage;

    @Column(name = "max_percentage")
    private Integer maxPercentage;

    @Column(name = "is_passing")
    private Boolean isPassing;

    public GradingScale() {
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getGradeLetter() {
        return gradeLetter;
    }

    public void setGradeLetter(String gradeLetter) {
        this.gradeLetter = gradeLetter;
    }

    public Integer getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(Integer minPercentage) {
        this.minPercentage = minPercentage;
    }

    public Integer getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(Integer maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    public Boolean getIsPassing() {
        return isPassing;
    }

    public void setIsPassing(Boolean isPassing) {
        this.isPassing = isPassing;
    }
}
