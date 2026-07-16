package com.certifypro.repository;

import com.certifypro.model.Question;
import com.certifypro.model.enums.Difficulty;
import com.certifypro.model.enums.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findByProgramId(Long programId, Pageable p);

    Page<Question> findByProgramIdAndDifficulty(Long programId, Difficulty d, Pageable p);

    Page<Question> findByProgramIdAndType(Long programId, QuestionType t, Pageable p);

    Page<Question> findByProgramIdAndDifficultyAndType(Long programId, Difficulty d, QuestionType t, Pageable p);
}
