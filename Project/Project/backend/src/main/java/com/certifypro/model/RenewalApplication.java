package com.certifypro.model;

import com.certifypro.model.enums.RenewalStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "renewal_application")
public class RenewalApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "renewal_id")
    private Long renewalId;

    @Column(name = "certificate_id")
    private Long certificateId;

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

    public RenewalApplication() {
    }

    public Long getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(Long renewalId) {
        this.renewalId = renewalId;
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Integer getCpdPointsSubmitted() {
        return cpdPointsSubmitted;
    }

    public void setCpdPointsSubmitted(Integer cpdPointsSubmitted) {
        this.cpdPointsSubmitted = cpdPointsSubmitted;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Long getReviewedById() {
        return reviewedById;
    }

    public void setReviewedById(Long reviewedById) {
        this.reviewedById = reviewedById;
    }

    public LocalDate getNewValidUntil() {
        return newValidUntil;
    }

    public void setNewValidUntil(LocalDate newValidUntil) {
        this.newValidUntil = newValidUntil;
    }

    public RenewalStatus getStatus() {
        return status;
    }

    public void setStatus(RenewalStatus status) {
        this.status = status;
    }
}
