package com.certifypro.question.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paper_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_question_id")
    private Long paperQuestionId;

    @Column(name = "paper_id")
    private Long paperId;

    // Intra-service relationship: PaperQuestion @ManyToOne Question.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "marks_allocated")
    private Integer marksAllocated;
}
