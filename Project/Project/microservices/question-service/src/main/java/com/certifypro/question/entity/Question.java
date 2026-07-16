package com.certifypro.question.entity;

import com.certifypro.question.common.Difficulty;
import com.certifypro.question.common.QuestionStatus;
import com.certifypro.question.common.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    // Cross-service reference (candidate-service program) — plain id, no FK.
    @Column(name = "program_id")
    private Long programId;

    @Column(name = "topic_tag")
    private String topicTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty;

    @Column(name = "question_text", columnDefinition = "text")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private QuestionType type;

    @Column(name = "marks")
    private Integer marks;

    // Cross-service reference (auth-service user) — plain id, no FK.
    @Column(name = "created_by_id")
    private Long createdById;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuestionStatus status;
}
