package com.certifypro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "paper_question")
public class PaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_question_id")
    private Long paperQuestionId;

    @Column(name = "paper_id")
    private Long paperId;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "marks_allocated")
    private Integer marksAllocated;

    public PaperQuestion() {
    }

    public Long getPaperQuestionId() {
        return paperQuestionId;
    }

    public void setPaperQuestionId(Long paperQuestionId) {
        this.paperQuestionId = paperQuestionId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    public Integer getMarksAllocated() {
        return marksAllocated;
    }

    public void setMarksAllocated(Integer marksAllocated) {
        this.marksAllocated = marksAllocated;
    }
}
