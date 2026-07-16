package com.certifypro.model;

import com.certifypro.model.enums.EligibilityStatus;
import com.certifypro.model.enums.EnrolmentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "program_enrolment")
public class ProgramEnrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrolment_id")
    private Long enrolmentId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "program_id")
    private Long programId;

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

    public ProgramEnrolment() {
    }

    public Long getEnrolmentId() {
        return enrolmentId;
    }

    public void setEnrolmentId(Long enrolmentId) {
        this.enrolmentId = enrolmentId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public LocalDate getEnrolmentDate() {
        return enrolmentDate;
    }

    public void setEnrolmentDate(LocalDate enrolmentDate) {
        this.enrolmentDate = enrolmentDate;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public Integer getAttemptsUsed() {
        return attemptsUsed;
    }

    public void setAttemptsUsed(Integer attemptsUsed) {
        this.attemptsUsed = attemptsUsed;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public EnrolmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrolmentStatus status) {
        this.status = status;
    }
}
