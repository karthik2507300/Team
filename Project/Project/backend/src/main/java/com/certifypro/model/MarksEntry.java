package com.certifypro.model;

import com.certifypro.model.enums.MarksStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "marks_entry")
public class MarksEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marks_id")
    private Long marksId;

    @Column(name = "script_id")
    private Long scriptId;

    @Column(name = "evaluator_id")
    private Long evaluatorId;

    @Column(name = "marks_awarded")
    private Integer marksAwarded;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "verified_by_id")
    private Long verifiedById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MarksStatus status;

    public MarksEntry() {
    }

    public Long getMarksId() {
        return marksId;
    }

    public void setMarksId(Long marksId) {
        this.marksId = marksId;
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Long evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public Integer getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(Integer marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public Long getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(Long verifiedById) {
        this.verifiedById = verifiedById;
    }

    public MarksStatus getStatus() {
        return status;
    }

    public void setStatus(MarksStatus status) {
        this.status = status;
    }
}
