package com.certifypro.question.entity;

import com.certifypro.question.common.PaperStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question_paper")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    private Long paperId;

    // Cross-service reference (exam-service exam window) — plain id, no FK.
    @Column(name = "window_id")
    private Long windowId;

    // Cross-service reference (candidate-service program) — plain id, no FK.
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

    // Cross-service reference (auth-service user) — plain id, no FK.
    @Column(name = "created_by_id")
    private Long createdById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaperStatus status;

    // Intra-service relationship: QuestionPaper @OneToMany List<PaperQuestion>.
    // Unidirectional via the shared paper_id column (PaperQuestion keeps a raw
    // paperId Long that stays writable for the addQuestions flow).
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", insertable = false, updatable = false)
    @OrderBy("sequenceOrder ASC")
    @Builder.Default
    private List<PaperQuestion> questions = new ArrayList<>();
}
