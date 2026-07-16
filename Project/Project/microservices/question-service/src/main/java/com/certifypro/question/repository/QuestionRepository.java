package com.certifypro.question.repository;

import com.certifypro.question.common.Difficulty;
import com.certifypro.question.common.QuestionType;
import com.certifypro.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByProgramId(Long programId, Pageable p);

    Page<Question> findByProgramIdAndDifficulty(Long programId, Difficulty d, Pageable p);

    Page<Question> findByProgramIdAndType(Long programId, QuestionType t, Pageable p);

    Page<Question> findByProgramIdAndDifficultyAndType(Long programId, Difficulty d, QuestionType t, Pageable p);
}
