package com.certifypro.model;

import com.certifypro.model.enums.ReEvalStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "re_evaluation_request")
public class ReEvaluationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "result_id")
    private Long resultId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "reason", columnDefinition = "text")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReEvalStatus status;

    public ReEvaluationRequest() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReEvalStatus getStatus() {
        return status;
    }

    public void setStatus(ReEvalStatus status) {
        this.status = status;
    }
}
