package com.certifypro.model;

import com.certifypro.model.enums.ScriptStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "script_allocation")
public class ScriptAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "script_id")
    private Long scriptId;

    @Column(name = "allocation_id")
    private Long allocationId;

    @Column(name = "evaluator_id")
    private Long evaluatorId;

    @Column(name = "paper_id")
    private Long paperId;

    @Column(name = "allocation_date")
    private LocalDate allocationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ScriptStatus status;

    public ScriptAllocation() {
    }

    public Long getScriptId() {
        return scriptId;
    }

    public void setScriptId(Long scriptId) {
        this.scriptId = scriptId;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Long evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public ScriptStatus getStatus() {
        return status;
    }

    public void setStatus(ScriptStatus status) {
        this.status = status;
    }
}
