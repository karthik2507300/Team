package com.certifypro.model;

import com.certifypro.model.enums.PaperStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "question_paper")
public class QuestionPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    private Long paperId;

    @Column(name = "window_id")
    private Long windowId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "paper_code")
    private String paperCode;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "instructions_ref", columnDefinition = "text")
    private String instructionsRef;

    @Column(name = "created_by_id")
    private Long createdById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaperStatus status;

    public QuestionPaper() {
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getWindowId() {
        return windowId;
    }

    public void setWindowId(Long windowId) {
        this.windowId = windowId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getInstructionsRef() {
        return instructionsRef;
    }

    public void setInstructionsRef(String instructionsRef) {
        this.instructionsRef = instructionsRef;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public PaperStatus getStatus() {
        return status;
    }

    public void setStatus(PaperStatus status) {
        this.status = status;
    }
}
